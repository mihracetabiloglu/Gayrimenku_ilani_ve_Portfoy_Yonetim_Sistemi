(function () {
  const API_BASE_URL = '';
  const TOKEN_KEY = 'emlakprime_token';
  const USER_KEY = 'emlakprime_user';
  const FAVORITES_KEY = 'emlakprime_favorites';
  const REMEMBER_KEY = 'emlakprime_remember_me';
  const REMEMBERED_LOGIN_KEY = 'emlakprime_remembered_login';
  const LAST_ACTIVE_KEY = 'emlakprime_last_activity';
  const LOGIN_TIME_KEY = 'loginTime';
  const EXPIRES_AT_KEY = 'expiresAt';
  const LEGACY_EXPIRES_KEY = 'emlakprime_auth_expires_at';
  const SESSION_TIMEOUT_MS = 2 * 60 * 60 * 1000;
  const REMEMBER_SESSION_TIMEOUT_MS = 7 * 24 * 60 * 60 * 1000;
  const TIMEOUT_MESSAGE = 'Oturum süreniz doldu, lütfen tekrar giriş yapın.';
  const AUTH_KEYS = [
    USER_KEY, TOKEN_KEY, 'token', 'jwtToken', 'roles', 'username', 'userId',
    LOGIN_TIME_KEY, EXPIRES_AT_KEY, LEGACY_EXPIRES_KEY, REMEMBER_KEY, LAST_ACTIVE_KEY
  ];

  function now() {
    return Date.now();
  }

  function storageWithToken() {
    if (localStorage.getItem('token') || localStorage.getItem(TOKEN_KEY)) return localStorage;
    if (sessionStorage.getItem('token') || sessionStorage.getItem(TOKEN_KEY)) return sessionStorage;
    return null;
  }

  function readStored(key) {
    return localStorage.getItem(key) || sessionStorage.getItem(key) || '';
  }

  function readNumber(key) {
    const value = Number(readStored(key));
    return Number.isFinite(value) ? value : 0;
  }

  function decodeTokenExpiry(token) {
    try {
      if (!token || token.split('.').length < 3) return 0;
      const payload = token.split('.')[1];
      const json = JSON.parse(atob(payload.replace(/-/g, '+').replace(/_/g, '/')));
      return json.exp ? json.exp * 1000 : 0;
    } catch (e) {
      return 0;
    }
  }

  function clearSession() {
    [localStorage, sessionStorage].forEach(function (storage) {
      AUTH_KEYS.forEach(function (key) { storage.removeItem(key); });
    });
  }

  function redirectToLogin() {
    const path = window.location.pathname.split('/').pop() || 'index.html';
    const query = window.location.search || '';
    const redirect = path === 'login.html' ? 'index.html' : path + query;
    window.location.href = 'login.html?redirect=' + encodeURIComponent(redirect);
  }

  function showTimeoutMessage() {
    try {
      sessionStorage.setItem('auth_timeout_message', TIMEOUT_MESSAGE);
    } catch (e) {}
    if (typeof window.showToast === 'function') {
      window.showToast(TIMEOUT_MESSAGE);
    } else {
      alert(TIMEOUT_MESSAGE);
    }
  }

  function consumeTimeoutMessage() {
    try {
      const msg = sessionStorage.getItem('auth_timeout_message');
      if (!msg) return;
      sessionStorage.removeItem('auth_timeout_message');
      setTimeout(function () {
        if (typeof window.showToast === 'function') window.showToast(msg);
        else {
          const el = document.getElementById('loginMsg') || document.getElementById('loginMessage');
          if (el) {
            el.style.color = '#c0392b';
            el.textContent = msg;
          }
        }
      }, 50);
    } catch (e) {}
  }

  function getToken() {
    return localStorage.getItem('token')
      || sessionStorage.getItem('token')
      || localStorage.getItem(TOKEN_KEY)
      || sessionStorage.getItem(TOKEN_KEY)
      || localStorage.getItem('jwtToken')
      || sessionStorage.getItem('jwtToken')
      || '';
  }

  function normalizeApiUrl(input) {
    if (typeof input !== 'string') return input;
    if (input.startsWith(API_BASE_URL)) return input;
    if (input.startsWith('/api/')) return API_BASE_URL + input.substring(4);
    if (input.startsWith('api/')) return API_BASE_URL + input.substring(3);
    return input;
  }

  function isApiRequest(input) {
    const url = typeof input === 'string' ? input : input && input.url;
    return typeof url === 'string' && (url.startsWith('/api/') || url.startsWith('api/') || url.startsWith(API_BASE_URL));
  }

  function requestMethod(input, init) {
    return String((init && init.method) || (input instanceof Request && input.method) || 'GET').toUpperCase();
  }

  function requestPath(input) {
    const rawUrl = typeof input === 'string' ? input : input && input.url;
    try {
      return new URL(rawUrl, window.location.origin).pathname;
    } catch (e) {
      return rawUrl || '';
    }
  }

  function isPublicRequest(input, init) {
    const path = requestPath(input);
    if (path === '/api/auth/login' || path === '/api/auth/register') return true;
    if (isPublicGet(input, init)) return true;
    return false;
  }

  function isPublicGet(input, init) {
    if (requestMethod(input, init) !== 'GET') return false;
    const path = requestPath(input);
    return [
      '/api/properties',
      '/api/listings',
      '/api/categories',
      '/api/cities',
      '/api/districts',
      '/api/property-types',
      '/api/neighborhoods',
      '/api/floors'
    ].some(function (publicPath) {
      return path === publicPath || path.startsWith(publicPath + '/');
    });
  }

  function isRemembered() {
    return readStored(REMEMBER_KEY) === 'true';
  }

  function getRememberedLogin() {
    return localStorage.getItem(REMEMBERED_LOGIN_KEY) || '';
  }

  function saveRememberedLogin(loginId, rememberMe) {
    if (rememberMe && loginId) {
      localStorage.setItem(REMEMBERED_LOGIN_KEY, loginId);
      return;
    }
    localStorage.removeItem(REMEMBERED_LOGIN_KEY);
  }

  function parseStoredJson(key, fallback) {
    const raw = readStored(key);
    if (!raw) return fallback;
    try { return JSON.parse(raw); } catch (e) { return fallback; }
  }

  function getUser() {
    return parseStoredJson(USER_KEY, null);
  }

  function getRoles() {
    const roles = parseStoredJson('roles', []);
    if (Array.isArray(roles)) return roles;
    if (typeof roles === 'string') return roles.split(',').map(function (role) { return role.trim(); }).filter(Boolean);
    return [];
  }

  function resolveExpiresAt(auth, loginTime, rememberMe) {
    const durationExpiresAt = loginTime + (rememberMe ? REMEMBER_SESSION_TIMEOUT_MS : SESSION_TIMEOUT_MS);
    const responseExpiresAt = Number(auth && auth.expiresAt) || 0;
    const jwtExpiresAt = decodeTokenExpiry(auth && auth.token);
    return [durationExpiresAt, responseExpiresAt, jwtExpiresAt]
      .filter(function (value) { return Number.isFinite(value) && value > 0; })
      .reduce(function (min, value) { return Math.min(min, value); });
  }

  function storeSession(auth, user) {
    const rememberMe = Boolean(auth && auth.rememberMe);
    const loginTime = now();
    const expiresAt = resolveExpiresAt(auth || {}, loginTime, rememberMe);
    const roles = (auth && auth.roles) || (user && user.roles) || [];
    const username = (auth && auth.username) || (user && user.username) || '';
    const userId = (auth && auth.userId) || (user && (user.userId || user.id)) || '';
    const token = (auth && auth.token) || '';
    const storage = rememberMe ? localStorage : sessionStorage;

    clearSession();
    saveRememberedLogin((auth && auth.loginId) || username, rememberMe);
    storage.setItem(TOKEN_KEY, token);
    storage.setItem('token', token);
    storage.setItem('roles', JSON.stringify(roles));
    storage.setItem('username', username);
    storage.setItem('userId', String(userId || ''));
    storage.setItem(LOGIN_TIME_KEY, String(loginTime));
    storage.setItem(EXPIRES_AT_KEY, String(expiresAt));
    storage.setItem(LEGACY_EXPIRES_KEY, String(expiresAt));
    storage.setItem(REMEMBER_KEY, String(rememberMe));
    storage.setItem(LAST_ACTIVE_KEY, String(loginTime));
    storage.setItem(USER_KEY, JSON.stringify({
      ...(user || {}),
      id: userId,
      userId: userId,
      username: username,
      roles: roles,
      token: token,
      loginTime: loginTime,
      expiresAt: expiresAt
    }));
  }

  function validateSession(options) {
    const opts = options || {};
    const token = getToken();
    if (!token) return false;

    const expiresAt = readNumber(EXPIRES_AT_KEY) || readNumber(LEGACY_EXPIRES_KEY) || decodeTokenExpiry(token);
    const expired = expiresAt && expiresAt <= now();
    if (expired) {
      clearSession();
      if (opts.redirect !== false) {
        showTimeoutMessage();
        redirectToLogin();
      }
      return false;
    }

    const storage = storageWithToken();
    if (storage) storage.setItem(LAST_ACTIVE_KEY, String(now()));
    return true;
  }

  function requireSession(options) {
    return validateSession({ redirect: options && options.redirect !== false });
  }

  function logout(redirectUrl) {
    clearSession();
    window.location.href = redirectUrl || 'index.html';
  }

  function isProtectedPage() {
    const page = (window.location.pathname.split('/').pop() || 'index.html').toLowerCase();
    return ['admin.html', 'hesabim.html', 'favoriler.html', 'ilan-ekle.html'].includes(page);
  }

  ['click', 'keydown', 'mousemove', 'scroll', 'touchstart'].forEach(function (eventName) {
    window.addEventListener(eventName, function () {
      if (!getToken()) return;
      const storage = storageWithToken();
      if (storage) storage.setItem(LAST_ACTIVE_KEY, String(now()));
    }, { passive: true });
  });

  window.EmlakAuth = {
    SESSION_TIMEOUT_MS: SESSION_TIMEOUT_MS,
    REMEMBER_SESSION_TIMEOUT_MS: REMEMBER_SESSION_TIMEOUT_MS,
    TIMEOUT_MESSAGE: TIMEOUT_MESSAGE,
    storeSession: storeSession,
    validateSession: validateSession,
    requireSession: requireSession,
    clearSession: clearSession,
    logout: logout,
    getToken: getToken,
    getUser: getUser,
    getRoles: getRoles,
    isRemembered: isRemembered,
    getRememberedLogin: getRememberedLogin,
    saveRememberedLogin: saveRememberedLogin,
    API_BASE_URL: API_BASE_URL,
    normalizeApiUrl: normalizeApiUrl
  };

  const nativeFetch = window.fetch.bind(window);
  window.fetch = function (input, init) {
    let requestInput = input;
    const requestInit = init ? { ...init } : {};
    let publicRequest = false;

    if (typeof input === 'string') {
      requestInput = normalizeApiUrl(input);
    } else if (input instanceof Request && isApiRequest(input)) {
      requestInput = new Request(normalizeApiUrl(input.url), input);
    }

    let skipAuthRedirect = false;

    if (isApiRequest(requestInput)) {
      publicRequest = isPublicRequest(requestInput, requestInit);
      if (!publicRequest && getToken() && !validateSession({ redirect: true })) {
        return Promise.reject(new Error(TIMEOUT_MESSAGE));
      }

      const headers = new Headers(requestInit.headers || (requestInput instanceof Request ? requestInput.headers : undefined));
      skipAuthRedirect = headers.get('X-Silent-Auth') === 'true';
      headers.delete('X-Silent-Auth');
      const token = getToken();
      if (token && !headers.has('Authorization')) {
        headers.set('Authorization', 'Bearer ' + token);
      }
      requestInit.headers = headers;
    }

    return nativeFetch(requestInput, requestInit).then(function (response) {
      if (isApiRequest(requestInput) && !publicRequest && !skipAuthRedirect && response.status === 401) {
        clearSession();
        showTimeoutMessage();
        redirectToLogin();
      }
      return response;
    });
  };

  consumeTimeoutMessage();
  validateSession({ redirect: isProtectedPage() });
  window.setInterval(function () { validateSession({ redirect: true }); }, 60000);
})();

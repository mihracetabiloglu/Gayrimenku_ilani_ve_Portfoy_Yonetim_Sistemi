(function () {
  const TOKEN_KEY = 'emlakprime_token';
  const USER_KEY = 'emlakprime_user';
  const FAVORITES_KEY = 'emlakprime_favorites';
  const EXPIRES_KEY = 'emlakprime_auth_expires_at';
  const REMEMBER_KEY = 'emlakprime_remember_me';
  const LAST_ACTIVE_KEY = 'emlakprime_last_activity';
  const DEFAULT_IDLE_TIMEOUT_MS = 30 * 60 * 1000;

  function now() {
    return Date.now();
  }

  function readNumber(key) {
    const value = Number(localStorage.getItem(key));
    return Number.isFinite(value) ? value : 0;
  }

  function decodeTokenExpiry(token) {
    try {
      const payload = token.split('.')[1];
      const json = JSON.parse(atob(payload.replace(/-/g, '+').replace(/_/g, '/')));
      return json.exp ? json.exp * 1000 : 0;
    } catch (e) {
      return 0;
    }
  }

  function clearSession() {
    localStorage.removeItem(USER_KEY);
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(FAVORITES_KEY);
    localStorage.removeItem(EXPIRES_KEY);
    localStorage.removeItem(REMEMBER_KEY);
    localStorage.removeItem(LAST_ACTIVE_KEY);
  }

  function redirectToLogin() {
    const path = window.location.pathname.split('/').pop() || 'index.html';
    const query = window.location.search || '';
    const redirect = path === 'login.html' ? 'index.html' : path + query;
    window.location.href = 'login.html?redirect=' + encodeURIComponent(redirect);
  }

  function getToken() {
    return localStorage.getItem(TOKEN_KEY) || '';
  }

  function isRemembered() {
    return localStorage.getItem(REMEMBER_KEY) === 'true';
  }

  function storeSession(auth, user) {
    const expiresAt = Number(auth.expiresAt) || decodeTokenExpiry(auth.token);
    localStorage.setItem(TOKEN_KEY, auth.token);
    localStorage.setItem(USER_KEY, JSON.stringify(user));
    localStorage.setItem(EXPIRES_KEY, String(expiresAt));
    localStorage.setItem(REMEMBER_KEY, String(Boolean(auth.rememberMe)));
    localStorage.setItem(LAST_ACTIVE_KEY, String(now()));
  }

  function validateSession(options) {
    const opts = options || {};
    const token = getToken();
    const expiresAt = readNumber(EXPIRES_KEY) || decodeTokenExpiry(token);
    const rememberMe = isRemembered();
    const lastActive = readNumber(LAST_ACTIVE_KEY) || now();
    const expired = token && expiresAt && expiresAt <= now();
    const idleExpired = token && !rememberMe && now() - lastActive > DEFAULT_IDLE_TIMEOUT_MS;

    if (expired || idleExpired) {
      clearSession();
      if (opts.redirect !== false) {
        alert(expired ? 'Oturum suresi doldu. Lutfen tekrar giris yapin.' : 'Uzun sure islem yapilmadigi icin oturum kapatildi.');
        redirectToLogin();
      }
      return false;
    }

    if (token) {
      localStorage.setItem(LAST_ACTIVE_KEY, String(now()));
    }
    return true;
  }

  ['click', 'keydown', 'mousemove', 'scroll', 'touchstart'].forEach(function (eventName) {
    window.addEventListener(eventName, function () {
      if (getToken()) localStorage.setItem(LAST_ACTIVE_KEY, String(now()));
    }, { passive: true });
  });

  window.EmlakAuth = {
    storeSession,
    validateSession,
    clearSession,
    getToken,
    isRemembered
  };

  validateSession({ redirect: false });
  window.setInterval(function () { validateSession({ redirect: true }); }, 60000);
})();

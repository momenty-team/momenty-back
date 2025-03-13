window.onload = function () {
  function getCookie(name) {
    let matches = document.cookie.match(new RegExp(
        "(?:^|; )" + name.replace(/([\.$?*|{}\(\)\[\]\\\/\+^])/g, '\\$1') + "=([^;]*)"
    ));
    return matches ? decodeURIComponent(matches[1]) : null;
  }

  function waitForElement(selector, callback, interval = 100, timeout = 5000) {
    const startTime = Date.now();
    const checkExist = setInterval(() => {
      let element = document.querySelector(selector);
      if (element) {
        clearInterval(checkExist);
        callback(element);
      }
      if (Date.now() - startTime > timeout) {
        clearInterval(checkExist);
      }
    }, interval);
  }

  setTimeout(() => {
    let accessToken = getCookie('access_token'); // 쿠키에서 access_token 가져오기
    let refreshToken = getCookie('refresh_token'); // 쿠키에서 refresh_token 가져오기
    let authorizeButton = document.querySelector('.authorize');

    if (authorizeButton) {
      authorizeButton.click(); // Authorize 창 자동 열기

      waitForElement('.modal-ux-content', () => {
        setTimeout(() => {
          let accessTokenInput = document.querySelector('input[name="access_token"]');
          if (accessTokenInput) {
            let currentAccessToken = accessTokenInput.value.trim();
            if (!currentAccessToken) {
              accessTokenInput.value = accessToken || "";
              accessTokenInput.dispatchEvent(new Event('input', { bubbles: true }));
            }
          }

          let refreshTokenInput = document.querySelector('input[name="refresh_token"]');
          if (refreshTokenInput) {
            let currentRefreshToken = refreshTokenInput.value.trim();
            if (!currentRefreshToken) {
              refreshTokenInput.value = refreshToken || "";
              refreshTokenInput.dispatchEvent(new Event('input', { bubbles: true }));
            }
          }

          waitForElement('.btn.modal-btn.auth.authorize.button', (modalAuthorizeButton) => {
            modalAuthorizeButton.click();
          });

          // **모든 API 요청이 쿠키를 포함하도록 설정**
          waitForElement('.execute', () => {
            let executeButtons = document.querySelectorAll('.execute');
            executeButtons.forEach(button => {
              button.addEventListener('click', function () {
                let request = new XMLHttpRequest();
                request.withCredentials = true; // 요청 시 쿠키 포함
              });
            });
          });

        }, 500);
      });
    }
  }, 1500);
};

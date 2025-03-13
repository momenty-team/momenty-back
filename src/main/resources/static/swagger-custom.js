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
          let refreshTokenInput = document.querySelector('input[name="refresh_token"]');

          // ✅ 사용자가 입력한 값을 우선 적용 (쿠키 값을 덮어쓰지 않음)
          if (accessTokenInput) {
            let currentAccessToken = accessTokenInput.value.trim();
            if (!currentAccessToken && accessToken) {
              accessTokenInput.value = accessToken;
              accessTokenInput.dispatchEvent(new Event('input', { bubbles: true }));
              accessTokenInput.dispatchEvent(new Event('change', { bubbles: true }));
            }
          }

          if (refreshTokenInput) {
            let currentRefreshToken = refreshTokenInput.value.trim();
            if (!currentRefreshToken && refreshToken) {
              refreshTokenInput.value = refreshToken;
              refreshTokenInput.dispatchEvent(new Event('input', { bubbles: true }));
              refreshTokenInput.dispatchEvent(new Event('change', { bubbles: true }));
            }
          }

          // ✅ Swagger UI의 토큰 저장 방식과 동기화 (localStorage 활용)
          setTimeout(() => {
            let modalAuthorizeButton = document.querySelector('.btn.modal-btn.auth.authorize.button');
            if (modalAuthorizeButton) {
              modalAuthorizeButton.click();

              // Swagger UI 내부에서 사용하는 `window.localStorage` 업데이트
              setTimeout(() => {
                if (accessToken) {
                  localStorage.setItem('swagger_access_token', accessToken);
                }
                if (refreshToken) {
                  localStorage.setItem('swagger_refresh_token', refreshToken);
                }
              }, 500);
            }
          }, 500);

          // ✅ MutationObserver로 토큰 값 변경 감지
          const observer = new MutationObserver(() => {
            let newAccessToken = getCookie('access_token');
            let newRefreshToken = getCookie('refresh_token');

            if (accessTokenInput && newAccessToken !== accessTokenInput.value) {
              accessTokenInput.value = newAccessToken || "";
              accessTokenInput.dispatchEvent(new Event('input', { bubbles: true }));
              accessTokenInput.dispatchEvent(new Event('change', { bubbles: true }));
            }

            if (refreshTokenInput && newRefreshToken !== refreshTokenInput.value) {
              refreshTokenInput.value = newRefreshToken || "";
              refreshTokenInput.dispatchEvent(new Event('input', { bubbles: true }));
              refreshTokenInput.dispatchEvent(new Event('change', { bubbles: true }));
            }
          });

          observer.observe(document.body, { childList: true, subtree: true });

          // ✅ 모든 API 요청이 쿠키를 포함하도록 설정
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

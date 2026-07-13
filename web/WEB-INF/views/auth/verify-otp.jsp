<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Verify OTP — Academic Management System</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
        <style>
            :root{--card-radius:14px;--accent:#0b1020}
            *{box-sizing:border-box;font-family:Inter,system-ui,Segoe UI,Roboto,"Helvetica Neue",Arial}
            body{
                min-height:100vh;display:flex;align-items:center;justify-content:center;
                margin:0;background:linear-gradient(180deg,#eef6ff 0%,#f3f8ff 100%)
            }
            .page-wrap{width:100%;max-width:520px;padding:40px}
            .card-box{
                background:#fff;border-radius:var(--card-radius);
                padding:36px 36px 28px;
                box-shadow:0 10px 30px rgba(15,23,42,.08);
                animation:fadeUp .5s ease-out
            }
            @keyframes fadeUp{
                from{opacity:0;transform:translateY(18px)}
                to{opacity:1;transform:translateY(0)}
            }
            .logo-circle{
                width:72px;height:72px;border-radius:50%;
                background:#0b1020;display:flex;align-items:center;
                justify-content:center;margin:0 auto;color:#fff;font-size:28px
            }
            .brand-title{font-size:22px;font-weight:600;text-align:center;margin-top:14px;color:#0b1020}
            .brand-sub{font-size:14px;color:#6b7280;text-align:center;margin-bottom:24px}

            /* OTP input grid */
            .otp-container{display:flex;gap:10px;justify-content:center;margin-bottom:24px}
            .otp-input{
                width:52px;height:60px;text-align:center;font-size:24px;font-weight:700;
                border:2px solid #e6eef8;border-radius:12px;background:#fbfdff;
                color:#0b1020;outline:none;transition:all .2s;caret-color:#3b82f6
            }
            .otp-input:focus{border-color:#3b82f6;box-shadow:0 0 0 3px rgba(59,130,246,.15)}
            .otp-input.filled{border-color:#10b981;background:#f0fdf4}
            .otp-input.error-border{border-color:#ef4444;animation:shake .4s}
            @keyframes shake{
                0%,100%{transform:translateX(0)}
                20%,60%{transform:translateX(-4px)}
                40%,80%{transform:translateX(4px)}
            }

            /* Timer */
            .timer-wrap{
                display:flex;align-items:center;justify-content:center;gap:8px;
                margin-bottom:20px;font-size:14px;color:#6b7280
            }
            .timer-badge{
                background:#fef3c7;color:#92400e;padding:4px 12px;
                border-radius:20px;font-weight:600;font-size:13px;
                display:inline-flex;align-items:center;gap:4px
            }
            .timer-badge.expired{background:#fee2e2;color:#991b1b}

            /* Buttons */
            .btn-primary-custom{
                background:#0b1020;color:#fff;border-radius:10px;
                padding:12px 16px;border:none;width:100%;font-weight:600;
                font-size:15px;cursor:pointer;transition:opacity .2s
            }
            .btn-primary-custom:hover{opacity:.92}
            .btn-primary-custom:disabled{opacity:.5;cursor:not-allowed}

            .resend-link{
                display:block;text-align:center;margin-top:14px;
                font-size:14px;color:#6b7280;text-decoration:none;transition:color .2s
            }
            .resend-link:hover{color:#0b1020}
            .resend-link.disabled{pointer-events:none;opacity:.4}

            .back-link{
                display:flex;align-items:center;gap:6px;
                color:#6b7280;text-decoration:none;font-size:14px;
                margin-top:18px;justify-content:center;transition:color .2s
            }
            .back-link:hover{color:#0b1020}
            .footer-note{text-align:center;color:#9ca3af;margin-top:18px;font-size:13px}
            .email-badge{
                background:#f0f4ff;border-radius:8px;padding:8px 14px;
                text-align:center;font-size:13px;color:#1e40af;margin-bottom:20px
            }
            @media(max-width:576px){
                .page-wrap{padding:18px}
                .card-box{padding:24px}
                .otp-input{width:44px;height:52px;font-size:20px}
                .otp-container{gap:6px}
            }
        </style>
    </head>
    <body>
        <div class="page-wrap">
            <div class="card-box">
                <div class="text-center">
                    <div class="logo-circle"><i class="bi bi-envelope-check" style="font-size:28px"></i></div>
                    <div class="brand-title">Verify OTP</div>
                    <div class="brand-sub">Enter the 6-digit code sent to your email</div>
                </div>

                <!-- Masked email display -->
                <c:if test="${not empty maskedEmail}">
                    <div class="email-badge">
                        <i class="bi bi-envelope me-1"></i>
                        Code sent to <strong>${maskedEmail}</strong>
                    </div>
                </c:if>

                <!-- Success message (resend) -->
                <c:if test="${not empty success}">
                    <div class="alert alert-success d-flex align-items-center" style="border-radius:10px;font-size:14px">
                        <i class="bi bi-check-circle-fill me-2"></i>${success}
                    </div>
                </c:if>

                <!-- Error message -->
                <c:if test="${not empty error}">
                    <div class="alert alert-danger d-flex align-items-center" style="border-radius:10px;font-size:14px">
                        <i class="bi bi-exclamation-triangle-fill me-2"></i>${error}
                    </div>
                </c:if>

                <!-- Timer -->
                <div class="timer-wrap">
                    <span>Time remaining:</span>
                    <span class="timer-badge" id="timerBadge">
                        <i class="bi bi-clock"></i>
                        <span id="timerDisplay">05:00</span>
                    </span>
                </div>

                <!-- OTP Form -->
                <form method="post" action="${pageContext.request.contextPath}/reset-password" id="otpForm">
                    <input type="hidden" name="action" value="verify">
                    <input type="hidden" name="otp" id="otpHidden">

                    <div class="otp-container" id="otpContainer">
                        <input type="text" class="otp-input" maxlength="1" data-index="0" inputmode="numeric" autocomplete="off">
                        <input type="text" class="otp-input" maxlength="1" data-index="1" inputmode="numeric" autocomplete="off">
                        <input type="text" class="otp-input" maxlength="1" data-index="2" inputmode="numeric" autocomplete="off">
                        <input type="text" class="otp-input" maxlength="1" data-index="3" inputmode="numeric" autocomplete="off">
                        <input type="text" class="otp-input" maxlength="1" data-index="4" inputmode="numeric" autocomplete="off">
                        <input type="text" class="otp-input" maxlength="1" data-index="5" inputmode="numeric" autocomplete="off">
                    </div>

                    <button type="submit" class="btn-primary-custom" id="verifyBtn" disabled>
                        <i class="bi bi-check-lg me-2"></i>Verify OTP
                    </button>
                </form>

                <!-- Resend OTP -->
                <form method="post" action="${pageContext.request.contextPath}/reset-password" style="margin:0">
                    <input type="hidden" name="action" value="resend">
                    <button type="submit" class="resend-link" id="resendBtn" style="background:none;border:none;width:100%">
                        <i class="bi bi-arrow-repeat me-1"></i>Resend OTP
                    </button>
                </form>

                <a href="${pageContext.request.contextPath}/forgot-password" class="back-link">
                    <i class="bi bi-arrow-left"></i> Try a different email
                </a>

                <p class="footer-note">&copy; <span id="year">2026</span> Academic Management System</p>
            </div>
        </div>

        <script>
            document.getElementById('year').textContent = new Date().getFullYear();

            // ============================
            //  OTP Input Handling
            // ============================
            var inputs = document.querySelectorAll('.otp-input');
            var otpHidden = document.getElementById('otpHidden');
            var verifyBtn = document.getElementById('verifyBtn');

            inputs.forEach(function(input, idx) {
                input.addEventListener('input', function(e) {
                    var val = this.value.replace(/[^0-9]/g, '');
                    this.value = val;

                    if (val && idx < 5) {
                        inputs[idx + 1].focus();
                    }
                    this.classList.toggle('filled', val.length > 0);
                    updateOTP();
                });

                input.addEventListener('keydown', function(e) {
                    if (e.key === 'Backspace' && !this.value && idx > 0) {
                        inputs[idx - 1].focus();
                        inputs[idx - 1].value = '';
                        inputs[idx - 1].classList.remove('filled');
                        updateOTP();
                    }
                });

                // Handle paste
                input.addEventListener('paste', function(e) {
                    e.preventDefault();
                    var paste = (e.clipboardData || window.clipboardData).getData('text').replace(/[^0-9]/g, '');
                    if (paste.length >= 6) {
                        for (var i = 0; i < 6; i++) {
                            inputs[i].value = paste[i];
                            inputs[i].classList.add('filled');
                        }
                        inputs[5].focus();
                        updateOTP();
                    }
                });
            });

            // Auto-focus first input
            inputs[0].focus();

            function updateOTP() {
                var otp = '';
                inputs.forEach(function(i) { otp += i.value; });
                otpHidden.value = otp;
                verifyBtn.disabled = otp.length < 6;
            }

            // ============================
            //  Countdown Timer (5 minutes)
            // ============================
            var totalSeconds = 5 * 60;
            var timerDisplay = document.getElementById('timerDisplay');
            var timerBadge = document.getElementById('timerBadge');

            var countdown = setInterval(function() {
                totalSeconds--;
                if (totalSeconds <= 0) {
                    clearInterval(countdown);
                    timerDisplay.textContent = '00:00';
                    timerBadge.classList.add('expired');
                    verifyBtn.disabled = true;
                    inputs.forEach(function(i) { i.disabled = true; });
                    return;
                }
                var m = Math.floor(totalSeconds / 60);
                var s = totalSeconds % 60;
                timerDisplay.textContent = String(m).padStart(2, '0') + ':' + String(s).padStart(2, '0');

                // Change color when < 60 seconds
                if (totalSeconds <= 60) {
                    timerBadge.style.background = '#fee2e2';
                    timerBadge.style.color = '#991b1b';
                }
            }, 1000);

            // Submit loading state
            document.getElementById('otpForm').addEventListener('submit', function() {
                verifyBtn.disabled = true;
                verifyBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Verifying...';
            });
        </script>
    </body>
</html>

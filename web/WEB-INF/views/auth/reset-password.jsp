<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Reset Password — Academic Management System</title>
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
                width:72px;height:72px;border-radius:50%;background:#0b1020;
                display:flex;align-items:center;justify-content:center;
                margin:0 auto;color:#fff;font-size:28px
            }
            .brand-title{font-size:22px;font-weight:600;text-align:center;margin-top:14px;color:#0b1020}
            .brand-sub{font-size:14px;color:#6b7280;text-align:center;margin-bottom:24px}
            .form-label{font-size:14px;color:#111827;margin-bottom:8px;font-weight:600}
            .input-icon-wrap{position:relative}
            .input-icon-wrap .icon-left{
                position:absolute;left:12px;top:50%;transform:translateY(-50%);
                color:#9aa4b2;font-size:16px
            }
            .input-icon-wrap .toggle-pwd{
                position:absolute;right:12px;top:50%;transform:translateY(-50%);
                color:#9aa4b2;cursor:pointer;font-size:16px;background:none;
                border:none;padding:0;transition:color .2s
            }
            .input-icon-wrap .toggle-pwd:hover{color:#0b1020}
            .form-control{
                border-radius:10px;border:1px solid #e6eef8;
                padding:12px 42px 12px 40px;background:#fbfdff;
                transition:border-color .2s,box-shadow .2s
            }
            .form-control:focus{
                border-color:#3b82f6;box-shadow:0 0 0 3px rgba(59,130,246,.12);outline:none
            }

            /* Strength meter */
            .strength-wrap{margin-top:8px}
            .strength-bar-bg{
                height:6px;background:#e5e7eb;border-radius:3px;overflow:hidden
            }
            .strength-bar{
                height:100%;width:0;border-radius:3px;
                transition:width .3s,background .3s
            }
            .strength-label{
                font-size:12px;margin-top:4px;font-weight:500;
                transition:color .3s
            }

            /* Match indicator */
            .match-indicator{
                font-size:13px;margin-top:6px;display:flex;
                align-items:center;gap:4px;opacity:0;transition:opacity .3s
            }
            .match-indicator.visible{opacity:1}
            .match-indicator.match{color:#10b981}
            .match-indicator.no-match{color:#ef4444}

            /* Requirements */
            .pwd-requirements{
                background:#f8fafc;border-radius:10px;padding:12px 14px;
                margin-bottom:20px;font-size:13px;color:#6b7280
            }
            .pwd-requirements ul{margin:6px 0 0;padding-left:18px}
            .pwd-requirements li{margin-bottom:2px}
            .pwd-requirements li.met{color:#10b981;text-decoration:line-through}

            .btn-primary-custom{
                background:#0b1020;color:#fff;border-radius:10px;
                padding:12px 16px;border:none;width:100%;font-weight:600;
                font-size:15px;cursor:pointer;transition:opacity .2s
            }
            .btn-primary-custom:hover{opacity:.92}
            .btn-primary-custom:disabled{opacity:.5;cursor:not-allowed}
            .back-link{
                display:flex;align-items:center;gap:6px;
                color:#6b7280;text-decoration:none;font-size:14px;
                margin-top:18px;justify-content:center;transition:color .2s
            }
            .back-link:hover{color:#0b1020}
            .footer-note{text-align:center;color:#9ca3af;margin-top:18px;font-size:13px}
            @media(max-width:576px){
                .page-wrap{padding:18px}
                .card-box{padding:24px}
            }
        </style>
    </head>
    <body>
        <div class="page-wrap">
            <div class="card-box">
                <div class="text-center">
                    <div class="logo-circle"><i class="bi bi-key" style="font-size:28px"></i></div>
                    <div class="brand-title">Reset Password</div>
                    <div class="brand-sub">Create a new password for your account</div>
                </div>

                <!-- Error message -->
                <c:if test="${not empty error}">
                    <div class="alert alert-danger d-flex align-items-center" style="border-radius:10px;font-size:14px">
                        <i class="bi bi-exclamation-triangle-fill me-2"></i>${error}
                    </div>
                </c:if>

                <div class="pwd-requirements">
                    <strong>Password requirements:</strong>
                    <ul>
                        <li id="reqLength">At least 6 characters</li>
                    </ul>
                </div>

                <form method="post" action="${pageContext.request.contextPath}/reset-password" id="resetForm" novalidate>
                    <input type="hidden" name="action" value="reset">

                    <!-- New Password -->
                    <div class="mb-3">
                        <label class="form-label">New Password</label>
                        <div class="input-icon-wrap">
                            <i class="icon-left bi bi-lock"></i>
                            <input type="password" name="newPassword" id="newPassword" class="form-control" 
                                   placeholder="Enter new password" autocomplete="new-password">
                            <button type="button" class="toggle-pwd" data-target="newPassword">
                                <i class="bi bi-eye"></i>
                            </button>
                        </div>
                        <!-- Strength meter -->
                        <div class="strength-wrap">
                            <div class="strength-bar-bg">
                                <div class="strength-bar" id="strengthBar"></div>
                            </div>
                            <div class="strength-label" id="strengthLabel">&nbsp;</div>
                        </div>
                    </div>

                    <!-- Confirm Password -->
                    <div class="mb-4">
                        <label class="form-label">Confirm Password</label>
                        <div class="input-icon-wrap">
                            <i class="icon-left bi bi-lock-fill"></i>
                            <input type="password" name="confirmPassword" id="confirmPassword" class="form-control" 
                                   placeholder="Re-enter your password" autocomplete="new-password">
                            <button type="button" class="toggle-pwd" data-target="confirmPassword">
                                <i class="bi bi-eye"></i>
                            </button>
                        </div>
                        <div class="match-indicator" id="matchIndicator">
                            <i class="bi"></i>
                            <span id="matchText"></span>
                        </div>
                    </div>

                    <button type="submit" class="btn-primary-custom" id="resetBtn" disabled>
                        <i class="bi bi-check2-circle me-2"></i>Reset Password
                    </button>
                </form>

                <a href="${pageContext.request.contextPath}/forgot-password" class="back-link">
                    <i class="bi bi-arrow-left"></i> Start over
                </a>

                <p class="footer-note">&copy; <span id="year">2026</span> Academic Management System</p>
            </div>
        </div>

        <script>
            document.getElementById('year').textContent = new Date().getFullYear();

            var newPwd     = document.getElementById('newPassword');
            var confirmPwd = document.getElementById('confirmPassword');
            var strengthBar   = document.getElementById('strengthBar');
            var strengthLabel = document.getElementById('strengthLabel');
            var matchInd   = document.getElementById('matchIndicator');
            var matchText  = document.getElementById('matchText');
            var matchIcon  = matchInd.querySelector('i');
            var resetBtn   = document.getElementById('resetBtn');
            var reqLength  = document.getElementById('reqLength');

            // ============================
            //  Toggle show/hide password
            // ============================
            document.querySelectorAll('.toggle-pwd').forEach(function(btn) {
                btn.addEventListener('click', function() {
                    var target = document.getElementById(this.dataset.target);
                    var icon = this.querySelector('i');
                    if (target.type === 'password') {
                        target.type = 'text';
                        icon.className = 'bi bi-eye-slash';
                    } else {
                        target.type = 'password';
                        icon.className = 'bi bi-eye';
                    }
                });
            });

            // ============================
            //  Password Strength Meter
            // ============================
            function calcStrength(pwd) {
                var score = 0;
                if (pwd.length >= 6) score++;
                if (pwd.length >= 10) score++;
                if (/[A-Z]/.test(pwd)) score++;
                if (/[0-9]/.test(pwd)) score++;
                if (/[^A-Za-z0-9]/.test(pwd)) score++;
                return score; // 0–5
            }

            var strengthConfig = [
                { label: '',            width: '0%',   color: '#e5e7eb' },
                { label: 'Weak',        width: '20%',  color: '#ef4444' },
                { label: 'Fair',        width: '40%',  color: '#f97316' },
                { label: 'Medium',      width: '60%',  color: '#eab308' },
                { label: 'Strong',      width: '80%',  color: '#22c55e' },
                { label: 'Very Strong', width: '100%', color: '#10b981' }
            ];

            newPwd.addEventListener('input', function() {
                var pwd = this.value;
                var score = calcStrength(pwd);

                if (pwd.length === 0) score = 0;

                var cfg = strengthConfig[score];
                strengthBar.style.width = cfg.width;
                strengthBar.style.background = cfg.color;
                strengthLabel.textContent = cfg.label;
                strengthLabel.style.color = cfg.color;

                // Requirements check
                reqLength.classList.toggle('met', pwd.length >= 6);

                checkMatch();
            });

            // ============================
            //  Confirm Password Match
            // ============================
            confirmPwd.addEventListener('input', checkMatch);

            function checkMatch() {
                var pwd1 = newPwd.value;
                var pwd2 = confirmPwd.value;

                if (pwd2.length === 0) {
                    matchInd.classList.remove('visible');
                    resetBtn.disabled = true;
                    return;
                }

                matchInd.classList.add('visible');

                if (pwd1 === pwd2) {
                    matchInd.className = 'match-indicator visible match';
                    matchIcon.className = 'bi bi-check-circle-fill';
                    matchText.textContent = 'Passwords match';
                } else {
                    matchInd.className = 'match-indicator visible no-match';
                    matchIcon.className = 'bi bi-x-circle-fill';
                    matchText.textContent = 'Passwords do not match';
                }

                // Enable submit only if match + length >= 6
                resetBtn.disabled = !(pwd1 === pwd2 && pwd1.length >= 6);
            }

            // ============================
            //  Submit loading state
            // ============================
            document.getElementById('resetForm').addEventListener('submit', function(e) {
                if (newPwd.value.length < 6 || newPwd.value !== confirmPwd.value) {
                    e.preventDefault();
                    return;
                }
                resetBtn.disabled = true;
                resetBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Resetting...';
            });
        </script>
    </body>
</html>

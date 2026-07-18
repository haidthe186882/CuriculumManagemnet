<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <c:set var="activeMenu" value="profile" />
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>My Profile — LTMS</title>
            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
            <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css"
                rel="stylesheet">
            <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap"
                rel="stylesheet">
            <%@ include file="/WEB-INF/views/common/styles.jsp" %>
                <style>
                    body {
                        font-family: 'Inter', sans-serif;
                        background-color: #f8fafc;
                    }

                    /* Centered card design matching the sketch */
                    .profile-sketch-card {
                        background: #ffffff;
                        border: 1.5px solid #e2e8f0;
                        border-radius: 16px;
                        box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.05), 0 8px 10px -6px rgba(0, 0, 0, 0.03);
                        max-width: 620px;
                        margin: 2rem auto;
                        padding: 2.25rem;
                    }

                    .avatar-circle {
                        width: 72px;
                        height: 72px;
                        border-radius: 50%;
                        background: linear-gradient(135deg, #0288d1, #26c6da);
                        color: white;
                        font-size: 2rem;
                        font-weight: 700;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        box-shadow: 0 4px 12px rgba(2, 136, 209, 0.15);
                        flex-shrink: 0;
                    }

                    .badge-role {
                        font-size: 0.78rem;
                        padding: 0.35rem 0.8rem;
                        border-radius: 20px;
                        font-weight: 600;
                        margin-top: 0.25rem;
                        display: inline-block;
                        border: 1px solid transparent;
                    }

                    .badge-admin {
                        background-color: rgba(13, 110, 253, 0.08);
                        border-color: rgba(13, 110, 253, 0.15);
                        color: #0d6efd;
                    }

                    .badge-designer {
                        background-color: rgba(253, 126, 20, 0.08);
                        border-color: rgba(253, 126, 20, 0.15);
                        color: #fd7e14;
                    }

                    .badge-reviewer {
                        background-color: rgba(111, 66, 193, 0.08);
                        border-color: rgba(111, 66, 193, 0.15);
                        color: #6f42c1;
                    }

                    .badge-teacher {
                        background-color: rgba(25, 135, 84, 0.08);
                        border-color: rgba(25, 135, 84, 0.15);
                        color: #198754;
                    }

                    .badge-student {
                        background-color: rgba(108, 117, 125, 0.08);
                        border-color: rgba(108, 117, 125, 0.15);
                        color: #6c757d;
                    }

                    .badge-guest {
                        background-color: rgba(220, 53, 69, 0.08);
                        border-color: rgba(220, 53, 69, 0.15);
                        color: #dc3545;
                    }

                    .form-label {
                        font-weight: 600;
                        font-size: 0.88rem;
                        color: #334155;
                        margin-bottom: 0.35rem;
                    }

                    .form-control {
                        border: 1.5px solid #cbd5e1;
                        border-radius: 8px;
                        padding: 0.55rem 0.75rem;
                        font-size: 0.9rem;
                        transition: all 0.2s;
                    }

                    .form-control:focus {
                        border-color: #0288d1;
                        box-shadow: 0 0 0 3px rgba(2, 136, 209, 0.1);
                    }

                    .form-control:disabled {
                        background-color: #f8fafc;
                        color: #64748b;
                        cursor: not-allowed;
                        border-color: #e2e8f0;
                    }

                    /* Password forms toggle layout */
                    .password-toggle-link {
                        display: inline-flex;
                        align-items: center;
                        font-weight: 600;
                        font-size: 0.92rem;
                        color: var(--accent);
                        text-decoration: none;
                        transition: color 0.15s;
                    }

                    .password-toggle-link:hover {
                        color: var(--accent-dark);
                    }

                    .eye-toggle-btn {
                        position: absolute;
                        right: 0.75rem;
                        top: 50%;
                        transform: translateY(-50%);
                        border: none;
                        background: none;
                        color: #64748b;
                        cursor: pointer;
                        padding: 0;
                    }

                    .input-icon-wrap {
                        position: relative;
                    }

                    .active-dot {
                        width: 14px;
                        height: 14px;
                        background-color: #10b981;
                        border: 2px solid #ffffff;
                        border-radius: 50%;
                        position: absolute;
                        top: 0;
                        right: 0;
                        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                    }
                </style>
        </head>

        <body>
            <%@ include file="/WEB-INF/views/common/sidebar.jsp" %>
                <div class="main-content">
                    <div class="topbar">
                        <div>
                            <div class="page-title">My Profile</div>
                            <div class="page-subtitle">View and update your personal information and security settings
                            </div>
                        </div>
                    </div>

                    <!-- Alert Messages -->
                    <c:if test="${not empty successMessage}">
                        <div class="alert d-flex align-items-center gap-2 mb-4 p-3 rounded-3"
                            style="background-color: rgba(16, 185, 129, 0.08); border: 1px solid rgba(16, 185, 129, 0.15); color: #065f46;"
                            role="alert">
                            <i class="bi bi-check-circle-fill fs-5"></i>
                            <div>${successMessage}</div>
                        </div>
                    </c:if>
                    <c:if test="${isGuest}">
                        <div class="alert d-flex align-items-center gap-2 mb-4 p-3 rounded-3"
                            style="background-color: rgba(245, 158, 11, 0.08); border: 1px solid rgba(245, 158, 11, 0.15); color: #78350f;"
                            role="alert">
                            <i class="bi bi-exclamation-triangle-fill fs-5"></i>
                            <div>Bạn đang đăng nhập bằng tài khoản Khách (Guest). Một số chức năng chỉnh sửa đã bị vô
                                hiệu hóa.</div>
                        </div>
                    </c:if>

                    <!-- Main single card matching user's sketch -->
                    <div class="profile-sketch-card">

                        <!-- Header: Avatar on left, Name & Role on right -->
                        <div class="d-flex align-items-center gap-3 mb-4">
                            <div>
                                <div class="avatar-circle">
                                    ${user.fullName.charAt(0)}
                                </div>
                            </div>
                            <div>
                                <h4 class="fw-bold mb-0" style="color: #0f172a; font-size: 1.35rem;">${user.fullName}
                                </h4>
                                <c:choose>
                                    <c:when test="${isGuest}">
                                        <span class="badge-role badge-guest">Guest Account</span>
                                    </c:when>
                                    <c:otherwise>
                                        <c:if test="${user.role.roleName == 'Admin'}">
                                            <span class="badge-role badge-admin"><i
                                                    class="bi bi-shield-lock-fill me-1"></i>Admin</span>
                                        </c:if>
                                        <c:if test="${user.role.roleName == 'Designer'}">
                                            <span class="badge-role badge-designer"><i
                                                    class="bi bi-pencil-square me-1"></i>Designer</span>
                                        </c:if>
                                        <c:if test="${user.role.roleName == 'Reviewer'}">
                                            <span class="badge-role badge-reviewer"><i
                                                    class="bi bi-clipboard-check me-1"></i>Reviewer</span>
                                        </c:if>
                                        <c:if test="${user.role.roleName == 'Teacher'}">
                                            <span class="badge-role badge-teacher"><i
                                                    class="bi bi-person-workspace me-1"></i>Teacher</span>
                                        </c:if>
                                        <c:if test="${user.role.roleName == 'Student'}">
                                            <span class="badge-role badge-student"><i
                                                    class="bi bi-mortarboard me-1"></i>Student</span>
                                        </c:if>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                        <hr style="border-color: #cbd5e1;" class="my-4">

                        <!-- Personal Details (Read-only) -->
                        <div class="mb-4">
                            <c:if test="${not empty error}">
                                <div class="alert alert-danger p-2 mb-3" style="font-size: 0.88rem;" role="alert">
                                    <i class="bi bi-exclamation-circle me-1"></i> ${error}
                                </div>
                            </c:if>

                            <div class="mb-3">
                                <label for="fullNameInput" class="form-label">Full name :</label>
                                <input type="text" id="fullNameInput" class="form-control" value="${user.fullName}"
                                    disabled>
                            </div>

                            <div class="mb-3">
                                <label for="emailInput" class="form-label">Email :</label>
                                <input type="email" id="emailInput" class="form-control" value="${user.email}" disabled>
                            </div>
                        </div>

                        <hr style="border-color: #cbd5e1;" class="my-4">

                        <!-- Toggle trigger for Change Password Form -->
                        <div class="mb-2">
                            <a href="javascript:void(0)" class="password-toggle-link" onclick="togglePasswordForm()">
                                <i class="bi bi-arrow-right" id="arrowIcon"
                                    style="font-size: 1.1rem; transition: transform 0.2s; display: inline-block;"></i>
                                <span class="ms-2">Change Password</span>
                            </a>
                        </div>

                        <!-- Form 2: Change Password (hidden by default, expands nicely) -->
                        <div id="passwordFormContainer" class="mt-4 ${empty errorPassword ? 'd-none' : ''}">
                            <div class="p-3 bg-light rounded-3 border" style="border-color: #cbd5e1 !important;">
                                <h6 class="fw-bold mb-3" style="color: #0f172a;"><i
                                        class="bi bi-shield-lock me-1"></i>Security Settings</h6>

                                <c:if test="${not empty errorPassword}">
                                    <div class="alert alert-danger p-2 mb-3" style="font-size: 0.88rem;" role="alert">
                                        <i class="bi bi-exclamation-circle me-1"></i> ${errorPassword}
                                    </div>
                                </c:if>

                                <form action="${pageContext.request.contextPath}/profile" method="post">
                                    <input type="hidden" name="action" value="changePassword">

                                    <div class="mb-3">
                                        <label for="oldPassword" class="form-label">Current Password</label>
                                        <div class="input-icon-wrap">
                                            <input type="password" id="oldPassword" name="oldPassword"
                                                class="form-control pe-5" required ${isGuest ? 'disabled' : '' }>
                                            <button type="button" class="eye-toggle-btn"
                                                onclick="togglePassword('oldPassword')">
                                                <i class="bi bi-eye" id="oldPassword-icon"></i>
                                            </button>
                                        </div>
                                    </div>

                                    <div class="mb-3">
                                        <label for="newPassword" class="form-label">New Password</label>
                                        <div class="input-icon-wrap">
                                            <input type="password" id="newPassword" name="newPassword"
                                                class="form-control pe-5" required ${isGuest ? 'disabled' : '' }>
                                            <button type="button" class="eye-toggle-btn"
                                                onclick="togglePassword('newPassword')">
                                                <i class="bi bi-eye" id="newPassword-icon"></i>
                                            </button>
                                        </div>
                                        <div class="form-text" style="font-size: 0.72rem; color: #94a3b8;">At least 6
                                            characters.</div>
                                    </div>

                                    <div class="mb-3">
                                        <label for="confirmPassword" class="form-label">Confirm New Password</label>
                                        <div class="input-icon-wrap">
                                            <input type="password" id="confirmPassword" name="confirmPassword"
                                                class="form-control pe-5" required ${isGuest ? 'disabled' : '' }>
                                            <button type="button" class="eye-toggle-btn"
                                                onclick="togglePassword('confirmPassword')">
                                                <i class="bi bi-eye" id="confirmPassword-icon"></i>
                                            </button>
                                        </div>
                                    </div>

                                    <button type="submit" class="btn btn-primary-custom w-100 mt-2" ${isGuest
                                        ? 'disabled' : '' }>
                                        <i class="bi bi-shield-check me-1"></i> Update Password
                                    </button>
                                </form>
                            </div>
                        </div>

                    </div>
                </div>

                <script>
                    function togglePasswordForm() {
                        const container = document.getElementById('passwordFormContainer');
                        const arrow = document.getElementById('arrowIcon');
                        if (container.classList.contains('d-none')) {
                            container.classList.remove('d-none');
                            arrow.style.transform = 'rotate(90deg)'; // Arrow points down when expanded
                        } else {
                            container.classList.add('d-none');
                            arrow.style.transform = 'rotate(0deg)'; // Arrow points right when collapsed
                        }
                    }

                    // Auto rotate arrow if form is pre-expanded due to error
                    window.onload = function () {
                        const container = document.getElementById('passwordFormContainer');
                        const arrow = document.getElementById('arrowIcon');
                        if (container && !container.classList.contains('d-none')) {
                            arrow.style.transform = 'rotate(90deg)';
                        }
                    };

                    function togglePassword(inputId) {
                        const input = document.getElementById(inputId);
                        const icon = document.getElementById(inputId + '-icon');
                        if (input.type === 'password') {
                            input.type = 'text';
                            icon.classList.remove('bi-eye');
                            icon.classList.add('bi-eye-slash');
                        } else {
                            input.type = 'password';
                            icon.classList.remove('bi-eye-slash');
                            icon.classList.add('bi-eye');
                        }
                    }
                </script>
        </body>

        </html>
<%@ page contentType="text/html;charset=UTF-8" language="java" import="model.User" %>
<%
	User u = null;
	if (session != null) {
		Object o = session.getAttribute("loggedUser");
		if (o instanceof model.User) u = (User) o;
	}
	if (u != null) {
		response.sendRedirect(request.getContextPath() + "/dashboard");
		return;
	}
%>
<!DOCTYPE html>
<html lang="vi">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Welcome to LTMS — Learning & Teaching Management System</title>
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
	<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
	<link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
	<%@ include file="/WEB-INF/views/common/styles.jsp" %>
	<style>
		body {
			background: #f0f4f9;
			color: #0f172a;
			font-family: 'Inter', sans-serif;
			min-height: 100vh;
			display: flex;
			align-items: center;
			justify-content: center;
			overflow-x: hidden;
			position: relative;
		}

		/* Soft Orange Ambient Backdrops */
		.bg-glow-1 {
			position: absolute;
			top: -10%;
			left: 20%;
			width: 600px;
			height: 600px;
			background: radial-gradient(circle, rgba(249, 115, 22, 0.12) 0%, rgba(255, 255, 255, 0) 70%);
			border-radius: 50%;
			pointer-events: none;
		}
		.bg-glow-2 {
			position: absolute;
			bottom: -10%;
			right: 15%;
			width: 650px;
			height: 650px;
			background: radial-gradient(circle, rgba(249, 93, 0, 0.08) 0%, rgba(255, 255, 255, 0) 70%);
			border-radius: 50%;
			pointer-events: none;
		}

		/* Main Hero White Card */
		.hero-card {
			background: #ffffff;
			border-radius: 28px;
			padding: 3.5rem 3rem;
			max-width: 640px;
			width: 100%;
			box-shadow: 0 20px 50px rgba(15, 23, 42, 0.06), 0 0 1px rgba(0, 0, 0, 0.08);
			border: 1px solid #e2e8f0;
			position: relative;
			z-index: 10;
			animation: fadeIn 0.8s ease-out;
		}

		@keyframes fadeIn {
			from { opacity: 0; transform: translateY(20px); }
			to { opacity: 1; transform: translateY(0); }
		}

		.brand-badge {
			display: inline-flex;
			align-items: center;
			gap: 10px;
			background: rgba(249, 115, 22, 0.08);
			border: 1px solid rgba(249, 115, 22, 0.25);
			padding: 8px 20px;
			border-radius: 100px;
			color: #ea580c;
			font-weight: 700;
			font-size: 0.9rem;
			letter-spacing: 0.04em;
			margin-bottom: 1.5rem;
		}

		.hero-title {
			font-weight: 800;
			font-size: 2.35rem;
			line-height: 1.25;
			color: #0f172a;
			margin-bottom: 0.75rem;
		}

		.hero-subtitle {
			color: #64748b;
			font-size: 1.025rem;
			line-height: 1.6;
			margin-bottom: 2.25rem;
		}

		/* Buttons */
		.btn-login-orange {
			background: #f95d00;
			color: #ffffff !important;
			font-weight: 700;
			font-size: 1rem;
			padding: 0.9rem 2.25rem;
			border-radius: 14px;
			border: none;
			box-shadow: 0 8px 20px rgba(249, 93, 0, 0.3);
			transition: all 0.3s ease;
			display: inline-flex;
			align-items: center;
			justify-content: center;
			gap: 10px;
			text-decoration: none;
		}
		.btn-login-orange:hover {
			background: #ea5200;
			box-shadow: 0 12px 28px rgba(249, 93, 0, 0.45);
			transform: translateY(-2px);
		}

		.btn-guest-outline {
			background: #ffffff;
			color: #ea580c !important;
			font-weight: 700;
			font-size: 1rem;
			padding: 0.9rem 2rem;
			border-radius: 14px;
			border: 2px solid #f97316;
			transition: all 0.3s ease;
			display: inline-flex;
			align-items: center;
			justify-content: center;
			gap: 10px;
			text-decoration: none;
		}
		.btn-guest-outline:hover {
			background: rgba(249, 115, 22, 0.06);
			transform: translateY(-2px);
		}

		/* Features grid pills */
		.features-grid {
			margin-top: 2.5rem;
			padding-top: 2rem;
			border-top: 1px solid #f1f5f9;
			display: grid;
			grid-template-columns: repeat(3, 1fr);
			gap: 1rem;
		}
		.feature-pill {
			background: #f8fafc;
			border: 1px solid #e2e8f0;
			border-radius: 16px;
			padding: 1.1rem 0.75rem;
			text-align: center;
			transition: all 0.2s ease;
		}
		.feature-pill:hover {
			background: #fff5ed;
			border-color: rgba(249, 115, 22, 0.3);
			transform: translateY(-2px);
		}
		.feature-icon {
			font-size: 1.4rem;
			color: #f95d00;
			margin-bottom: 0.5rem;
		}
		.feature-label {
			font-size: 0.83rem;
			font-weight: 700;
			color: #334155;
			line-height: 1.35;
		}

		.redirect-note {
			margin-top: 1.75rem;
			font-size: 0.85rem;
			color: #94a3b8;
		}
	</style>
</head>
<body>
	<!-- Ambient Backdrops -->
	<div class="bg-glow-1"></div>
	<div class="bg-glow-2"></div>

	<!-- Main White Card -->
	<div class="hero-card text-center">
		<!-- Brand Logo Badge -->
		<div class="brand-badge">
			<i class="bi bi-mortarboard-fill fs-5"></i>
			<span>LTMS SYSTEM</span>
		</div>

		<!-- Main Heading & Subtitle -->
		<h1 class="hero-title">Welcome to LTMS</h1>
		<p class="hero-subtitle">
			Learning & Teaching Management System for Curriculums, Syllabuses & Prerequisites
		</p>

		<!-- Primary Action Buttons -->
		<div class="d-flex flex-column flex-sm-row justify-content-center gap-3">
			<a class="btn-login-orange" href="${pageContext.request.contextPath}/login">
				<i class="bi bi-box-arrow-in-right"></i> Log In
			</a>
			<a class="btn-guest-outline" href="${pageContext.request.contextPath}/login?action=guest">
				<i class="bi bi-person-workspace"></i> Explore as Guest
			</a>
		</div>

		<!-- Feature Highlights -->
		<div class="features-grid">
			<div class="feature-pill">
				<i class="bi bi-journal-bookmark-fill feature-icon"></i>
				<div class="feature-label">Curriculums & Syllabuses</div>
			</div>
			<div class="feature-pill">
				<i class="bi bi-diagram-3-fill feature-icon"></i>
				<div class="feature-label">Prerequisite Roadmaps</div>
			</div>
			<div class="feature-pill">
				<i class="bi bi-shield-check feature-icon"></i>
				<div class="feature-label">Role Governance</div>
			</div>
		</div>

		<!-- Auto-redirect notice -->
		<div class="redirect-note">
			<i class="bi bi-info-circle me-1"></i> Or, if you already have a session, you will be redirected.
		</div>
	</div>

	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

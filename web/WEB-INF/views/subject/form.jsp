<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="activeMenu" value="subject"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>New Subject — LTMS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <%@ include file="/WEB-INF/views/common/styles.jsp" %>
    <style>
        /* ---- Page-specific additions, layered on top of the shared LTMS tokens ---- */

        .subject-create-grid {
            display: grid;
            grid-template-columns: 1.5fr 1fr;
            gap: 1.5rem;
            align-items: start;
        }
        @media (max-width: 991px) {
            .subject-create-grid { grid-template-columns: 1fr; }
            .preview-col { order: -1; }
        }

        .form-section + .form-section {
            margin-top: 1.75rem;
            padding-top: 1.5rem;
            border-top: 1px solid var(--border);
        }
        .form-section-eyebrow {
            font-size: 0.72rem;
            font-weight: 700;
            letter-spacing: 1px;
            text-transform: uppercase;
            color: var(--accent-dark);
            margin-bottom: 0.9rem;
            display: flex;
            align-items: center;
            gap: 0.4rem;
        }
        .required-dot {
            color: var(--accent);
            font-weight: 700;
        }
        .field-hint {
            font-size: 0.76rem;
            color: var(--muted);
            margin-top: 0.3rem;
        }
        input#subjectCode {
            font-family: 'JetBrains Mono', 'Courier New', monospace;
            letter-spacing: 1px;
            text-transform: uppercase;
            font-weight: 600;
        }
        .credit-chips {
            display: flex;
            gap: 0.4rem;
            margin-top: 0.5rem;
            flex-wrap: wrap;
        }
        .credit-chip {
            border: 1px solid var(--border);
            background: #fff;
            color: #374151;
            font-size: 0.78rem;
            font-weight: 600;
            border-radius: 999px;
            padding: 0.25rem 0.7rem;
            cursor: pointer;
            transition: all .12s;
        }
        .credit-chip:hover { border-color: var(--accent); color: var(--accent-dark); }
        .credit-chip.active {
            background: linear-gradient(135deg, var(--accent), var(--accent-dark));
            border-color: transparent;
            color: #fff;
        }
        .char-counter {
            font-size: 0.74rem;
            color: var(--muted);
            text-align: right;
            margin-top: 0.3rem;
        }

        /* Sticky live preview */
        .preview-col { position: sticky; top: 1.5rem; }
        .preview-card {
            background: #ffffff;
            border-radius: 16px;
            border: 1px dashed rgba(255,106,0,0.35);
            box-shadow: 0 12px 30px rgba(15,23,42,0.06);
            padding: 1.4rem;
        }
        .preview-eyebrow {
            display: inline-flex;
            align-items: center;
            gap: 0.4rem;
            font-size: 0.7rem;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.6px;
            color: var(--muted);
            margin-bottom: 1rem;
        }
        .preview-eyebrow .dot {
            width: 6px; height: 6px; border-radius: 50%;
            background: var(--accent);
            display: inline-block;
            animation: pulse 1.6s infinite;
        }
        @keyframes pulse {
            0%, 100% { opacity: 1; }
            50% { opacity: 0.25; }
        }
        .preview-code {
            display: inline-block;
            font-family: 'JetBrains Mono', 'Courier New', monospace;
            font-weight: 700;
            font-size: 0.85rem;
            color: var(--accent-dark);
            background: rgba(255,106,0,0.07);
            border-radius: 8px;
            padding: 0.2rem 0.6rem;
            margin-bottom: 0.6rem;
            letter-spacing: 0.5px;
        }
        .preview-name {
            font-size: 1.25rem;
            font-weight: 700;
            color: #111827;
            line-height: 1.3;
            margin-bottom: 0.15rem;
        }
        .preview-english {
            font-size: 0.85rem;
            font-style: italic;
            color: var(--muted);
            margin-bottom: 1rem;
        }
        .preview-meta {
            display: flex;
            gap: 0.5rem;
            margin-bottom: 1rem;
            flex-wrap: wrap;
        }
        .preview-chip {
            font-size: 0.76rem;
            font-weight: 600;
            border-radius: 999px;
            padding: 0.3rem 0.7rem;
            display: inline-flex;
            align-items: center;
            gap: 0.3rem;
        }
        .preview-chip.credits { background: rgba(16,185,129,0.08); color: #10b981; }
        .preview-chip.dept    { background: rgba(99,102,241,0.08); color: #6366f1; }
        .preview-desc {
            font-size: 0.85rem;
            color: #4b5563;
            line-height: 1.5;
            border-top: 1px solid var(--border);
            padding-top: 0.9rem;
        }
        .preview-placeholder { color: #9ca3af; font-style: italic; }
        @media (prefers-reduced-motion: reduce) {
            .preview-eyebrow .dot { animation: none; }
        }
    </style>
</head>
<body>
<%@ include file="/WEB-INF/views/common/sidebar.jsp" %>
<div class="main-content">
    <div class="topbar">
        <div>
            <div class="page-title">New Subject</div>
            <div class="page-subtitle">Add a subject to the curriculum catalog</div>
        </div>
        <a href="${pageContext.request.contextPath}/subject/list" class="btn btn-secondary-custom">
            <i class="bi bi-arrow-left me-1"></i>Back to Subjects
        </a>
    </div>

    <c:if test="${not empty errorMessage}">
        <div class="mb-3" style="background: rgba(239,68,68,0.06); border:1px solid rgba(239,68,68,0.18); border-radius:10px; padding: 0.8rem 1rem; color:#b91c1c;">
            <i class="bi bi-exclamation-triangle me-1"></i>${errorMessage}
        </div>
    </c:if>

    <div class="subject-create-grid">

        <!-- ===== Form column ===== -->
        <div class="form-col">
            <div class="card-dark p-4">
                <form id="subjectForm" method="post" action="${pageContext.request.contextPath}/subject/create">
                    <input type="hidden" name="action" value="create">

                    <!-- Identity -->
                    <div class="form-section">
                        <div class="form-section-eyebrow"><i class="bi bi-fingerprint"></i> Identity</div>
                        <div class="row">
                            <div class="col-md-5 mb-3">
                                <label class="detail-label">Subject Code <span class="required-dot">*</span></label>
                                <input type="text" id="subjectCode" name="subjectCode" class="form-control form-control-dark w-100"
                                       placeholder="CS101" maxlength="20" value="${subject.subjectCode}" required>
                                <div class="field-hint">Unique identifier used across curriculums and syllabuses.</div>
                            </div>
                            <div class="col-md-3 mb-3">
                                <label class="detail-label">Credits <span class="required-dot">*</span></label>
                                <input type="number" id="credits" name="credits" class="form-control form-control-dark w-100"
                                       min="1" max="12" placeholder="3" value="${subject.credits}" required>
                                <div class="credit-chips" id="creditChips">
                                    <span class="credit-chip" data-value="1">1</span>
                                    <span class="credit-chip" data-value="2">2</span>
                                    <span class="credit-chip" data-value="3">3</span>
                                    <span class="credit-chip" data-value="4">4</span>
                                    <span class="credit-chip" data-value="5">5</span>
                                </div>
                            </div>
                            <div class="col-md-4 mb-3">
                                <label class="detail-label">Department</label>
                                <select id="department" name="department" class="form-control form-control-dark w-100">
                                    <option value="">-- Select major --</option>
                                    <c:forEach var="m" items="${majors}">
                                        <option value="${m.majorName}" ${subject.department == m.majorName ? 'selected' : ''}>${m.majorName}</option>
                                    </c:forEach>
                                </select>
                                <div class="field-hint">Which major owns this subject.</div>
                            </div>
                        </div>
                    </div>

                    <!-- Naming -->
                    <div class="form-section">
                        <div class="form-section-eyebrow"><i class="bi bi-card-text"></i> Naming</div>
                        <div class="mb-3">
                            <label class="detail-label">Subject Name <span class="required-dot">*</span></label>
                            <input type="text" id="subjectName" name="subjectName" class="form-control form-control-dark w-100"
                                   placeholder="Introduction to Programming" value="${subject.subjectName}" required>
                        </div>
                        <div class="mb-0">
                            <label class="detail-label">English Name</label>
                            <input type="text" id="englishName" name="englishName" class="form-control form-control-dark w-100"
                                   placeholder="Optional — used on international transcripts" value="${subject.englishName}">
                        </div>
                    </div>

                    <!-- Description -->
                    <div class="form-section">
                        <div class="form-section-eyebrow"><i class="bi bi-text-paragraph"></i> Description</div>
                        <textarea id="description" name="description" class="form-control form-control-dark w-100"
                                  placeholder="What does this subject cover? Keep it to a couple of sentences."
                                  rows="4">${subject.description}</textarea>
                        <div class="char-counter"><span id="descCount">0</span> characters</div>
                    </div>

                    <div class="mt-3 d-flex gap-2">
                        <button type="submit" class="btn btn-primary-custom"><i class="bi bi-save me-1"></i>Create Subject</button>
                        <a href="${pageContext.request.contextPath}/subject/list" class="btn btn-secondary-custom">Cancel</a>
                    </div>
                </form>
            </div>
        </div>

        <!-- ===== Live preview column ===== -->
        <div class="preview-col">
            <div class="preview-card">
                <div class="preview-eyebrow"><span class="dot"></span> Preview — not saved yet</div>
                <span class="preview-code" id="pvCode">CODE</span>
                <div class="preview-name" id="pvName">Subject name</div>
                <div class="preview-english" id="pvEnglish">English name</div>
                <div class="preview-meta">
                    <span class="preview-chip credits"><i class="bi bi-mortarboard"></i> <span id="pvCredits">0</span> credits</span>
                    <span class="preview-chip dept"><i class="bi bi-building"></i> <span id="pvDept">No department</span></span>
                </div>
                <div class="preview-desc" id="pvDesc">This is how the subject will appear in the catalog once created.</div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
(function () {
    var codeInput  = document.getElementById('subjectCode');
    var creditsInput = document.getElementById('credits');
    var deptSelect = document.getElementById('department');
    var nameInput  = document.getElementById('subjectName');
    var engInput   = document.getElementById('englishName');
    var descArea   = document.getElementById('description');

    var pvCode    = document.getElementById('pvCode');
    var pvName    = document.getElementById('pvName');
    var pvEnglish = document.getElementById('pvEnglish');
    var pvCredits = document.getElementById('pvCredits');
    var pvDept    = document.getElementById('pvDept');
    var pvDesc    = document.getElementById('pvDesc');
    var descCount = document.getElementById('descCount');

    function val(el, fallback) {
        var v = el.value && el.value.trim();
        return v ? v : fallback;
    }

    function refreshPreview() {
        pvCode.textContent = val(codeInput, 'CODE');
        pvName.textContent = val(nameInput, 'Subject name');
        pvName.classList.toggle('preview-placeholder', !nameInput.value.trim());

        var eng = engInput.value.trim();
        pvEnglish.textContent = eng ? eng : 'No English name set';
        pvEnglish.classList.toggle('preview-placeholder', !eng);

        pvCredits.textContent = creditsInput.value ? creditsInput.value : '0';
        pvDept.textContent = deptSelect.value ? deptSelect.value : 'No department';

        var desc = descArea.value.trim();
        pvDesc.textContent = desc ? desc : 'This is how the subject will appear in the catalog once created.';
        pvDesc.classList.toggle('preview-placeholder', !desc);

        descCount.textContent = descArea.value.length;

        // highlight matching credit chip
        document.querySelectorAll('.credit-chip').forEach(function (chip) {
            chip.classList.toggle('active', chip.dataset.value === creditsInput.value);
        });
    }

    // Auto-uppercase the subject code as the user types
    codeInput.addEventListener('input', function () {
        var pos = this.selectionStart;
        this.value = this.value.toUpperCase();
        this.setSelectionRange(pos, pos);
        refreshPreview();
    });

    [creditsInput, deptSelect, nameInput, engInput, descArea].forEach(function (el) {
        el.addEventListener('input', refreshPreview);
        el.addEventListener('change', refreshPreview);
    });

    document.querySelectorAll('.credit-chip').forEach(function (chip) {
        chip.addEventListener('click', function () {
            creditsInput.value = chip.dataset.value;
            refreshPreview();
        });
    });

    refreshPreview();
})();
</script>
</body>
</html>

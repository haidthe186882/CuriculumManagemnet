<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- ==================== IMPORT FROM EXCEL SECTION ==================== -->
<div class="card mb-4" style="background: linear-gradient(135deg, #fff7ed 0%, #ffffff 100%); border: 1px solid rgba(255,106,0,0.15); border-radius: 14px;">
    <div class="card-body p-4">
        <div class="d-flex align-items-center mb-3">
            <div style="width:42px; height:42px; border-radius:10px; background:linear-gradient(135deg,#ff6a00,#e05500); display:flex; align-items:center; justify-content:center; margin-right:12px;">
                <i class="bi bi-file-earmark-excel" style="color:#fff; font-size:1.2rem;"></i>
            </div>
            <div>
                <h6 class="mb-0" style="font-weight:700; color:#111827;">Import from Excel</h6>
                <small style="color:var(--muted);">Upload a syllabus Excel file to auto-fill all fields below</small>
            </div>
        </div>
        <div class="row align-items-end g-3">
            <div class="col-md-8">
                <label class="detail-label mb-1">Select Excel File (.xls, .xlsx)</label>
                <input type="file" id="importExcelFile" accept=".xls,.xlsx"
                       class="form-control form-control-dark" style="padding:0.5rem 0.75rem;">
            </div>
            <div class="col-md-4">
                <button type="button" id="btnImportExcel" class="btn btn-primary-custom w-100" onclick="importExcel()">
                    <i class="bi bi-cloud-upload me-1"></i>Load Data From Excel
                </button>
            </div>
        </div>
        <div id="importStatus" class="mt-2" style="display:none;"></div>
    </div>
</div>

<!-- ==================== MAIN CREATE FORM ==================== -->
<form method="post" action="${pageContext.request.contextPath}/syllabus/list" id="syllabusCreateForm" enctype="multipart/form-data">
    <input type="hidden" name="action" value="create">

    <!-- ========== TAB NAVIGATION ========== -->
    <ul class="nav nav-tabs" id="syllabusTabs" role="tablist" style="border-bottom:2px solid var(--border); margin-bottom:0;">
        <li class="nav-item" role="presentation">
            <button class="nav-link active" id="tab-basic" data-bs-toggle="tab" data-bs-target="#pane-basic"
                    type="button" role="tab" style="font-weight:600; font-size:0.9rem; color:#374151; border-radius:10px 10px 0 0;">
                <i class="bi bi-info-circle me-1"></i>Basic Info
            </button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link" id="tab-clo" data-bs-toggle="tab" data-bs-target="#pane-clo"
                    type="button" role="tab" style="font-weight:600; font-size:0.9rem; color:#374151; border-radius:10px 10px 0 0;">
                <i class="bi bi-mortarboard me-1"></i>CLOs <span class="badge bg-secondary ms-1" id="cloCount">0</span>
            </button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link" id="tab-session" data-bs-toggle="tab" data-bs-target="#pane-session"
                    type="button" role="tab" style="font-weight:600; font-size:0.9rem; color:#374151; border-radius:10px 10px 0 0;">
                <i class="bi bi-calendar3 me-1"></i>Sessions <span class="badge bg-secondary ms-1" id="sessionCount">0</span>
            </button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link" id="tab-material" data-bs-toggle="tab" data-bs-target="#pane-material"
                    type="button" role="tab" style="font-weight:600; font-size:0.9rem; color:#374151; border-radius:10px 10px 0 0;">
                <i class="bi bi-book me-1"></i>Materials <span class="badge bg-secondary ms-1" id="materialCount">0</span>
            </button>
        </li>
    </ul>

    <!-- ========== TAB CONTENT ========== -->
    <div class="tab-content p-4" id="syllabusTabContent" style="background:#fff; border:1px solid var(--border); border-top:none; border-radius:0 0 14px 14px;">

        <!-- ===== TAB 1: BASIC INFO ===== -->
        <div class="tab-pane fade show active" id="pane-basic" role="tabpanel">
            <div class="mb-3">
                <label class="detail-label">Subject *</label>
                <select name="subjectId" id="subjectId" class="form-select form-select-dark w-100" required>
                    <option value="">-- Select Subject --</option>
                    <c:forEach var="s" items="${subjects}">
                        <option value="${s.subjectId}">${s.subjectCode} — ${s.subjectName}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="mb-3">
                <label class="detail-label">Syllabus Name *</label>
                <input type="text" name="syllabusName" id="syllabusName" class="form-control form-control-dark w-100" required>
            </div>
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label class="detail-label">English Name</label>
                    <input type="text" name="englishName" id="englishName" class="form-control form-control-dark w-100">
                </div>
                <div class="col-md-3 mb-3">
                    <label class="detail-label">Version</label>
                    <input type="text" name="version" id="version" class="form-control form-control-dark w-100" value="1.0">
                </div>
                <div class="col-md-3 mb-3">
                    <label class="detail-label">Min Avg to Pass</label>
                    <input type="number" step="0.1" name="minAvgMarkToPass" id="minAvgMarkToPass" class="form-control form-control-dark w-100" value="5.0">
                </div>
            </div>
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label class="detail-label">Time Allocation</label>
                    <input type="text" name="timeAllocation" id="timeAllocation" class="form-control form-control-dark w-100" placeholder="e.g. 45h LT + 45h TH">
                </div>
                <div class="col-md-6 mb-3">
                    <label class="detail-label">Scoring Scale</label>
                    <input type="text" name="scoringScale" id="scoringScale" class="form-control form-control-dark w-100" value="10">
                </div>
            </div>
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label class="detail-label">Decision No</label>
                    <input type="text" name="decisionNo" id="decisionNo" class="form-control form-control-dark w-100">
                </div>
                <div class="col-md-6 mb-3">
                    <label class="detail-label">Approved Date</label>
                    <input type="date" name="approvedDate" id="approvedDate" class="form-control form-control-dark w-100">
                </div>
            </div>
            <div class="mb-3">
                <label class="detail-label">Description</label>
                <textarea name="description" id="description" class="form-control form-control-dark w-100" rows="3"></textarea>
            </div>
            <div class="mb-3">
                <label class="detail-label">Student Tasks</label>
                <textarea name="studentTasks" id="studentTasks" class="form-control form-control-dark w-100" rows="3"></textarea>
            </div>
            <div class="mb-3">
                <label class="detail-label">Tools</label>
                <textarea name="tools" id="tools" class="form-control form-control-dark w-100" rows="2"></textarea>
            </div>
        </div>

        <!-- ===== TAB 2: CLOs ===== -->
        <div class="tab-pane fade" id="pane-clo" role="tabpanel">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h6 class="mb-0" style="font-weight:700;">Course Learning Outcomes</h6>
                <button type="button" class="btn btn-sm btn-primary-custom" onclick="addCloRow()">
                    <i class="bi bi-plus-lg me-1"></i>Add CLO
                </button>
            </div>
            <div class="table-responsive">
                <table class="table table-dark-custom mb-0" id="cloTable">
                    <thead>
                        <tr>
                            <th style="width:50px; background-color:var(--accent)!important; color:#fff!important; text-transform:none; padding:0.7rem;">#</th>
                            <th style="width:150px; background-color:var(--accent)!important; color:#fff!important; text-transform:none; padding:0.7rem;">CLO Code</th>
                            <th style="background-color:var(--accent)!important; color:#fff!important; text-transform:none; padding:0.7rem;">Description</th>
                            <th style="width:60px; background-color:var(--accent)!important; color:#fff!important; text-transform:none; padding:0.7rem;"></th>
                        </tr>
                    </thead>
                    <tbody id="cloBody">
                        <!-- dynamic rows -->
                    </tbody>
                </table>
            </div>
            <div id="cloEmpty" class="text-center py-4 text-muted">
                <i class="bi bi-inbox" style="font-size:2rem; display:block; margin-bottom:0.5rem;"></i>
                No CLOs added yet. Click "Add CLO" or import from Excel.
            </div>
        </div>

        <!-- ===== TAB 3: SESSIONS ===== -->
        <div class="tab-pane fade" id="pane-session" role="tabpanel">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h6 class="mb-0" style="font-weight:700;">Sessions</h6>
                <button type="button" class="btn btn-sm btn-primary-custom" onclick="addSessionRow()">
                    <i class="bi bi-plus-lg me-1"></i>Add Session
                </button>
            </div>
            <div class="table-responsive">
                <table class="table table-dark-custom mb-0" id="sessionTable">
                    <thead>
                        <tr>
                            <th style="width:45px; background-color:var(--accent)!important; color:#fff!important; text-transform:none; padding:0.6rem; font-size:0.78rem;">#</th>
                            <th style="width:170px; background-color:var(--accent)!important; color:#fff!important; text-transform:none; padding:0.6rem; font-size:0.78rem;">Topic</th>
                            <th style="width:120px; background-color:var(--accent)!important; color:#fff!important; text-transform:none; padding:0.6rem; font-size:0.78rem;">L/T Type</th>
                            <th style="width:80px; background-color:var(--accent)!important; color:#fff!important; text-transform:none; padding:0.6rem; font-size:0.78rem;">LO</th>
                            <th style="width:80px; background-color:var(--accent)!important; color:#fff!important; text-transform:none; padding:0.6rem; font-size:0.78rem;">ITU</th>
                            <th style="width:130px; background-color:var(--accent)!important; color:#fff!important; text-transform:none; padding:0.6rem; font-size:0.78rem;">Materials</th>
                            <th style="width:130px; background-color:var(--accent)!important; color:#fff!important; text-transform:none; padding:0.6rem; font-size:0.78rem;">Tasks</th>
                            <th style="width:100px; background-color:var(--accent)!important; color:#fff!important; text-transform:none; padding:0.6rem; font-size:0.78rem;">URLs</th>
                            <th style="width:40px; background-color:var(--accent)!important; color:#fff!important; text-transform:none; padding:0.6rem;"></th>
                        </tr>
                    </thead>
                    <tbody id="sessionBody">
                        <!-- dynamic rows -->
                    </tbody>
                </table>
            </div>
            <div id="sessionEmpty" class="text-center py-4 text-muted">
                <i class="bi bi-inbox" style="font-size:2rem; display:block; margin-bottom:0.5rem;"></i>
                No sessions added yet. Click "Add Session" or import from Excel.
            </div>
        </div>

        <!-- ===== TAB 4: MATERIALS ===== -->
        <div class="tab-pane fade" id="pane-material" role="tabpanel">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h6 class="mb-0" style="font-weight:700;">Materials / References</h6>
                <button type="button" class="btn btn-sm btn-primary-custom" onclick="addMaterialRow()">
                    <i class="bi bi-plus-lg me-1"></i>Add Material
                </button>
            </div>
            <div id="materialCards">
                <!-- dynamic material cards -->
            </div>
            <div id="materialEmpty" class="text-center py-4 text-muted">
                <i class="bi bi-inbox" style="font-size:2rem; display:block; margin-bottom:0.5rem;"></i>
                No materials added yet. Click "Add Material" or import from Excel.
            </div>
        </div>

    </div>

    <!-- ========== SUBMIT BUTTONS ========== -->
    <div class="mt-4 d-flex gap-2">
        <button type="submit" class="btn btn-primary-custom btn-lg">
            <i class="bi bi-save me-1"></i>Create Syllabus
        </button>
        <a href="${pageContext.request.contextPath}/syllabus/list" class="btn btn-secondary-custom btn-lg">
            Cancel
        </a>
    </div>
</form>

<!-- ==================== STYLES ==================== -->
<style>
    .nav-tabs .nav-link {
        color: #6b7280;
        border: 1px solid transparent;
        transition: all 0.2s;
    }
    .nav-tabs .nav-link:hover {
        color: var(--accent);
        border-color: transparent;
        background: rgba(255,106,0,0.04);
    }
    .nav-tabs .nav-link.active {
        color: var(--accent) !important;
        background: #fff !important;
        border-color: var(--border) var(--border) #fff !important;
        font-weight: 700 !important;
    }
    .dynamic-input {
        background: #fff;
        border: 1px solid var(--border);
        border-radius: 6px;
        padding: 0.35rem 0.5rem;
        font-size: 0.85rem;
        width: 100%;
        color: #111827;
        transition: border-color 0.15s;
    }
    .dynamic-input:focus {
        outline: none;
        border-color: var(--accent);
        box-shadow: 0 0 0 2px rgba(255,106,0,0.08);
    }
    .btn-remove-row {
        background: rgba(239,68,68,0.06);
        border: none;
        color: #ef4444;
        border-radius: 6px;
        padding: 0.3rem 0.5rem;
        font-size: 0.85rem;
        cursor: pointer;
        transition: all 0.15s;
    }
    .btn-remove-row:hover {
        background: rgba(239,68,68,0.15);
    }
    .material-card {
        background: #fafbfc;
        border: 1px solid var(--border);
        border-radius: 12px;
        padding: 1rem;
        margin-bottom: 0.75rem;
        position: relative;
        transition: box-shadow 0.15s;
    }
    .material-card:hover {
        box-shadow: 0 4px 12px rgba(0,0,0,0.04);
    }
    .material-card .card-number {
        position: absolute;
        top: 0.75rem;
        left: 0.75rem;
        width: 28px;
        height: 28px;
        border-radius: 50%;
        background: var(--accent);
        color: #fff;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 0.75rem;
        font-weight: 700;
    }
    .import-loading {
        display: inline-block;
        width: 18px;
        height: 18px;
        border: 2px solid rgba(255,106,0,0.2);
        border-top-color: var(--accent);
        border-radius: 50%;
        animation: spin 0.6s linear infinite;
        vertical-align: middle;
        margin-right: 6px;
    }
    @keyframes spin { to { transform: rotate(360deg); } }
</style>

<!-- ==================== JAVASCRIPT ==================== -->
<script>
    const ctx = '${pageContext.request.contextPath}';

    /* ====== IMPORT EXCEL ====== */
    function importExcel() {
        const fileInput = document.getElementById('importExcelFile');
        const statusEl = document.getElementById('importStatus');
        const btn = document.getElementById('btnImportExcel');

        if (!fileInput.files || !fileInput.files[0]) {
            showStatus('warning', '<i class="bi bi-exclamation-triangle me-1"></i>Please select an Excel file first.');
            return;
        }

        const formData = new FormData();
        formData.append('excelFile', fileInput.files[0]);
        formData.append('importAction', 'importExcel');

        btn.disabled = true;
        btn.innerHTML = '<span class="import-loading"></span>Importing...';
        showStatus('info', '<span class="import-loading"></span>Reading Excel file...');

        fetch(ctx + '/syllabus/importExcel', { method: 'POST', body: formData })
            .then(r => {
                // Check if response is JSON
                const contentType = r.headers.get('content-type') || '';
                if (!r.ok) {
                    return r.text().then(txt => { throw new Error('Server error (' + r.status + '): ' + txt.substring(0, 200)); });
                }
                if (!contentType.includes('application/json')) {
                    return r.text().then(txt => { throw new Error('Server returned HTML instead of JSON. Check server logs. Response: ' + txt.substring(0, 200)); });
                }
                return r.json();
            })
            .then(data => {
                btn.disabled = false;
                btn.innerHTML = '<i class="bi bi-cloud-upload me-1"></i>Load Data From Excel';

                if (data.error) {
                    showStatus('danger', '<i class="bi bi-x-circle me-1"></i>' + data.error);
                    return;
                }

                fillFormFromImport(data);

                let summary = 'Imported: ';
                const parts = [];
                if (data.syllabusName) parts.push('Basic info');
                if (data.clos && data.clos.length) parts.push(data.clos.length + ' CLOs');
                if (data.sessions && data.sessions.length) parts.push(data.sessions.length + ' Sessions');
                if (data.materials && data.materials.length) parts.push(data.materials.length + ' Materials');
                summary += parts.join(', ') || 'No data found';

                showStatus('success', '<i class="bi bi-check-circle me-1"></i>' + summary + '. Review and edit below before saving.');
            })
            .catch(err => {
                btn.disabled = false;
                btn.innerHTML = '<i class="bi bi-cloud-upload me-1"></i>Load Data From Excel';
                showStatus('danger', '<i class="bi bi-x-circle me-1"></i>Import failed: ' + err.message);
            });
    }

    function showStatus(type, html) {
        const el = document.getElementById('importStatus');
        el.style.display = 'block';
        const colors = { success: '#065f46', danger: '#991b1b', warning: '#92400e', info: '#1e40af' };
        const bgs    = { success: 'rgba(16,185,129,0.08)', danger: 'rgba(239,68,68,0.08)', warning: 'rgba(245,158,11,0.08)', info: 'rgba(59,130,246,0.08)' };
        el.style.background = bgs[type] || bgs.info;
        el.style.color = colors[type] || colors.info;
        el.style.padding = '0.6rem 1rem';
        el.style.borderRadius = '8px';
        el.style.fontSize = '0.88rem';
        el.style.fontWeight = '500';
        el.innerHTML = html;
    }

    function fillFormFromImport(data) {
        // Basic info
        if (data.syllabusName) document.getElementById('syllabusName').value = data.syllabusName;
        if (data.englishName) document.getElementById('englishName').value = data.englishName;
        if (data.version) document.getElementById('version').value = data.version;
        if (data.description) document.getElementById('description').value = data.description;
        if (data.timeAllocation) document.getElementById('timeAllocation').value = data.timeAllocation;
        if (data.studentTasks) document.getElementById('studentTasks').value = data.studentTasks;
        if (data.tools) document.getElementById('tools').value = data.tools;
        if (data.scoringScale) document.getElementById('scoringScale').value = data.scoringScale;
        if (data.minAvgMarkToPass) document.getElementById('minAvgMarkToPass').value = data.minAvgMarkToPass;
        if (data.decisionNo) document.getElementById('decisionNo').value = data.decisionNo;
        if (data.approvedDate) document.getElementById('approvedDate').value = data.approvedDate;

        // CLOs
        if (data.clos && data.clos.length > 0) {
            document.getElementById('cloBody').innerHTML = '';
            data.clos.forEach(clo => addCloRow(clo.code, clo.description));
        }

        // Sessions
        if (data.sessions && data.sessions.length > 0) {
            document.getElementById('sessionBody').innerHTML = '';
            data.sessions.forEach(s => addSessionRow(s.no, s.topic, s.type, s.lo, s.itu, s.materials, s.tasks, s.urls));
        }

        // Materials
        if (data.materials && data.materials.length > 0) {
            document.getElementById('materialCards').innerHTML = '';
            data.materials.forEach(m => addMaterialRow(m.description, m.author, m.publisher, m.edition, m.isbn, m.link, m.notes, m.isMain));
        }
    }

    /* ====== CLO DYNAMIC TABLE ====== */
    function addCloRow(code, desc) {
        code = code || '';
        desc = desc || '';
        const tbody = document.getElementById('cloBody');
        const idx = tbody.rows.length + 1;
        const tr = document.createElement('tr');
        tr.innerHTML =
            '<td style="padding:0.5rem; text-align:center; font-weight:600; color:var(--muted);">' + idx + '</td>' +
            '<td style="padding:0.4rem;"><input type="text" name="cloCode[]" class="dynamic-input" value="' + escHtml(code) + '" placeholder="e.g. CLO1"></td>' +
            '<td style="padding:0.4rem;"><input type="text" name="cloDesc[]" class="dynamic-input" value="' + escHtml(desc) + '" placeholder="Description..."></td>' +
            '<td style="padding:0.4rem; text-align:center;"><button type="button" class="btn-remove-row" onclick="removeRow(this, \'clo\')"><i class="bi bi-trash"></i></button></td>';
        tbody.appendChild(tr);
        updateCounts();
    }

    /* ====== SESSION DYNAMIC TABLE ====== */
    function addSessionRow(no, topic, type, lo, itu, mats, tasks, urls) {
        const tbody = document.getElementById('sessionBody');
        const idx = tbody.rows.length + 1;
        no = no || idx;
        topic = topic || ''; type = type || ''; lo = lo || '';
        itu = itu || ''; mats = mats || ''; tasks = tasks || ''; urls = urls || '';

        const tr = document.createElement('tr');
        tr.innerHTML =
            '<td style="padding:0.35rem;"><input type="text" name="sessionNo[]" class="dynamic-input" value="' + no + '" style="width:40px; text-align:center;"></td>' +
            '<td style="padding:0.35rem;"><textarea name="sessionTopic[]" class="dynamic-input" rows="2">' + escHtml(topic) + '</textarea></td>' +
            '<td style="padding:0.35rem;"><input type="text" name="sessionType[]" class="dynamic-input" value="' + escHtml(type) + '"></td>' +
            '<td style="padding:0.35rem;"><input type="text" name="sessionLO[]" class="dynamic-input" value="' + escHtml(lo) + '"></td>' +
            '<td style="padding:0.35rem;"><input type="text" name="sessionITU[]" class="dynamic-input" value="' + escHtml(itu) + '"></td>' +
            '<td style="padding:0.35rem;"><textarea name="sessionMaterial[]" class="dynamic-input" rows="2">' + escHtml(mats) + '</textarea></td>' +
            '<td style="padding:0.35rem;"><textarea name="sessionTask[]" class="dynamic-input" rows="2">' + escHtml(tasks) + '</textarea></td>' +
            '<td style="padding:0.35rem;"><input type="text" name="sessionURL[]" class="dynamic-input" value="' + escHtml(urls) + '"></td>' +
            '<td style="padding:0.35rem; text-align:center;"><button type="button" class="btn-remove-row" onclick="removeRow(this, \'session\')"><i class="bi bi-trash"></i></button></td>';
        tbody.appendChild(tr);
        updateCounts();
    }

    /* ====== MATERIAL DYNAMIC CARDS ====== */
    function addMaterialRow(desc, author, publisher, edition, isbn, link, notes, isMain) {
        desc = desc || ''; author = author || ''; publisher = publisher || '';
        edition = edition || ''; isbn = isbn || ''; link = link || '';
        notes = notes || ''; isMain = isMain || false;

        const container = document.getElementById('materialCards');
        const idx = container.children.length + 1;

        const card = document.createElement('div');
        card.className = 'material-card';
        card.innerHTML =
            '<div class="card-number">' + idx + '</div>' +
            '<button type="button" class="btn-remove-row" style="position:absolute; top:0.75rem; right:0.75rem;" onclick="removeMaterial(this)"><i class="bi bi-trash"></i></button>' +
            '<div class="row g-2" style="margin-left:32px;">' +
                '<div class="col-md-8 mb-2">' +
                    '<label class="detail-label" style="font-size:0.72rem;">Description *</label>' +
                    '<input type="text" name="matDesc[]" class="dynamic-input" value="' + escHtml(desc) + '" placeholder="Material description">' +
                '</div>' +
                '<div class="col-md-4 mb-2">' +
                    '<label class="detail-label" style="font-size:0.72rem;">Author</label>' +
                    '<input type="text" name="matAuthor[]" class="dynamic-input" value="' + escHtml(author) + '">' +
                '</div>' +
                '<div class="col-md-3 mb-2">' +
                    '<label class="detail-label" style="font-size:0.72rem;">Publisher</label>' +
                    '<input type="text" name="matPublisher[]" class="dynamic-input" value="' + escHtml(publisher) + '">' +
                '</div>' +
                '<div class="col-md-2 mb-2">' +
                    '<label class="detail-label" style="font-size:0.72rem;">Edition</label>' +
                    '<input type="text" name="matEdition[]" class="dynamic-input" value="' + escHtml(edition) + '">' +
                '</div>' +
                '<div class="col-md-3 mb-2">' +
                    '<label class="detail-label" style="font-size:0.72rem;">ISBN</label>' +
                    '<input type="text" name="matIsbn[]" class="dynamic-input" value="' + escHtml(isbn) + '">' +
                '</div>' +
                '<div class="col-md-4 mb-2">' +
                    '<label class="detail-label" style="font-size:0.72rem;">Link / URL</label>' +
                    '<input type="text" name="matLink[]" class="dynamic-input" value="' + escHtml(link) + '">' +
                '</div>' +
                '<div class="col-md-8 mb-2">' +
                    '<label class="detail-label" style="font-size:0.72rem;">Notes</label>' +
                    '<input type="text" name="matNotes[]" class="dynamic-input" value="' + escHtml(notes) + '">' +
                '</div>' +
                '<div class="col-md-4 mb-2 d-flex align-items-end">' +
                    '<div class="form-check">' +
                        '<input type="checkbox" name="matMain[]" class="form-check-input" ' + (isMain ? 'checked' : '') + '>' +
                        '<label class="form-check-label" style="font-size:0.82rem; color:#374151;">Main Material</label>' +
                    '</div>' +
                '</div>' +
            '</div>';

        container.appendChild(card);
        updateCounts();
    }

    /* ====== REMOVE HANDLERS ====== */
    function removeRow(btn, type) {
        const tr = btn.closest('tr');
        tr.style.transition = 'opacity 0.2s, transform 0.2s';
        tr.style.opacity = '0';
        tr.style.transform = 'translateX(20px)';
        setTimeout(() => {
            tr.remove();
            reindexTable(type);
            updateCounts();
        }, 200);
    }

    function removeMaterial(btn) {
        const card = btn.closest('.material-card');
        card.style.transition = 'opacity 0.2s, transform 0.2s';
        card.style.opacity = '0';
        card.style.transform = 'translateX(20px)';
        setTimeout(() => {
            card.remove();
            reindexMaterials();
            updateCounts();
        }, 200);
    }

    function reindexTable(type) {
        const tbody = document.getElementById(type + 'Body');
        if (!tbody) return;
        Array.from(tbody.rows).forEach((row, i) => {
            row.cells[0].textContent = i + 1;
            // Update sessionNo input if sessions
            if (type === 'session') {
                const noInput = row.querySelector('input[name="sessionNo[]"]');
                if (noInput && !noInput.value) noInput.value = i + 1;
            }
        });
    }

    function reindexMaterials() {
        const cards = document.querySelectorAll('#materialCards .material-card');
        cards.forEach((card, i) => {
            const num = card.querySelector('.card-number');
            if (num) num.textContent = i + 1;
        });
    }

    function updateCounts() {
        const cloCount = document.getElementById('cloBody').rows.length;
        const sessionCount = document.getElementById('sessionBody').rows.length;
        const matCount = document.querySelectorAll('#materialCards .material-card').length;

        document.getElementById('cloCount').textContent = cloCount;
        document.getElementById('sessionCount').textContent = sessionCount;
        document.getElementById('materialCount').textContent = matCount;

        document.getElementById('cloEmpty').style.display = cloCount > 0 ? 'none' : 'block';
        document.getElementById('sessionEmpty').style.display = sessionCount > 0 ? 'none' : 'block';
        document.getElementById('materialEmpty').style.display = matCount > 0 ? 'none' : 'block';
    }

    function escHtml(str) {
        if (!str) return '';
        return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&#39;');
    }

    // Initialize counts on page load
    document.addEventListener('DOMContentLoaded', updateCounts);
</script>

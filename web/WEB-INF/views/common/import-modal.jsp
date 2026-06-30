<%-- Import Excel Modal — shared across curriculum pages --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- ===== IMPORT EXCEL MODAL ===== -->
<div class="modal fade" id="importExcelModal" tabindex="-1" aria-labelledby="importExcelModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered" style="max-width:520px;">
        <div class="modal-content border-0 shadow-lg" style="border-radius:16px;overflow:hidden;">

            <!-- Header -->
            <div class="modal-header border-0 pb-0" style="background:#fff;padding:1.5rem 1.5rem 0.5rem;">
                <div>
                    <h5 class="modal-title fw-bold" id="importExcelModalLabel" style="color:#111827;font-size:1.05rem;">
                        <i class="bi bi-file-earmark-excel me-2" style="color:#16a34a;"></i>Import curriculum from Excel
                    </h5>
                    <p class="mb-0 mt-1" style="font-size:0.8rem;color:#6b7280;">
                        Upload an <code>.xlsx</code> file — Sheet1 (info), Sheet2 (PLOs), Sheet3 (subjects)
                    </p>
                </div>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <!-- Body -->
            <div class="modal-body" style="background:#fff;padding:1.25rem 1.5rem;">

                <!-- Sheet map pills -->
                <div class="d-flex gap-2 mb-3">
                    <span class="import-sheet-pill"><i class="bi bi-1-circle me-1"></i>Curriculum info</span>
                    <span class="import-sheet-pill"><i class="bi bi-2-circle me-1"></i>PLOs</span>
                    <span class="import-sheet-pill"><i class="bi bi-3-circle me-1"></i>Subjects</span>
                </div>

                <!-- Drop zone -->
                <div id="importDropZone"
                     onclick="document.getElementById('importFileInput').click()"
                     ondragover="importHandleDrag(event,true)"
                     ondragleave="importHandleDrag(event,false)"
                     ondrop="importHandleDrop(event)"
                     class="import-drop-zone" role="button" tabindex="0"
                     onkeydown="if(event.key==='Enter')this.click()">
                    <i class="bi bi-cloud-upload import-drop-icon"></i>
                    <p class="import-drop-text">Drop your Excel file here</p>
                    <span class="import-drop-sub">or click to browse — .xlsx only</span>
                </div>
                <input type="file" id="importFileInput" accept=".xlsx,.xls" style="display:none"
                       onchange="importHandleFile(this)">

                <!-- File preview (hidden until file chosen) -->
                <div id="importPreview" style="display:none;margin-top:1rem;">
                    <div class="d-flex align-items-center gap-3 p-3 import-preview-box">
                        <div class="import-file-icon"><i class="bi bi-file-earmark-spreadsheet"></i></div>
                        <div style="flex:1;min-width:0;">
                            <div class="import-filename" id="importFilename"></div>
                            <div class="import-filesize" id="importFilesize"></div>
                        </div>
                        <button type="button" class="btn-close btn-close-sm" onclick="importClearFile()" aria-label="Remove"></button>
                    </div>
                </div>

                <!-- Progress (hidden until import starts) -->
                <div id="importProgress" style="display:none;margin-top:1rem;">
                    <div class="d-flex justify-content-between mb-1">
                        <span style="font-size:0.8rem;color:#6b7280;" id="importProgressMsg">Parsing curriculum info…</span>
                        <span style="font-size:0.8rem;color:#6b7280;" id="importProgressPct">0%</span>
                    </div>
                    <div class="import-progress-track">
                        <div class="import-progress-fill" id="importProgressFill" style="width:0%"></div>
                    </div>
                </div>

                <!-- Success banner (hidden) -->
                <div id="importSuccess" style="display:none;margin-top:1rem;" class="import-success-banner">
                    <i class="bi bi-check-circle-fill me-2"></i>Curriculum imported — data pre-filled below. Review then click <strong>Create</strong>.
                </div>

                <!-- Error banner (hidden) -->
                <div id="importError" style="display:none;margin-top:1rem;" class="import-error-banner">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i><span id="importErrorMsg"></span>
                </div>
            </div>

            <!-- Footer -->
            <div class="modal-footer border-0 pt-0" style="background:#fff;padding:0.75rem 1.5rem 1.5rem;">
                <button type="button" class="btn btn-secondary-custom" data-bs-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-primary-custom" id="importSubmitBtn"
                        disabled onclick="importSubmit()">
                    <i class="bi bi-upload me-1"></i>Import
                </button>
            </div>

        </div>
    </div>
</div>

<!-- Hidden form that POSTs the file -->
<form id="importHiddenForm" method="post"
      action="${pageContext.request.contextPath}/curriculum/importExcel"
      enctype="multipart/form-data" style="display:none;">
    <c:if test="${not empty curriculum.curriculumId}">
        <input type="hidden" name="curriculumId" value="${curriculum.curriculumId}"/>
    </c:if>
    <input type="file" name="excelFile" id="importHiddenFile">
</form>

<style>
.import-sheet-pill{background:#f3f4f6;color:#374151;border-radius:20px;padding:3px 10px;font-size:0.75rem;font-weight:500;}
.import-drop-zone{border:2px dashed #d1d5db;border-radius:12px;padding:2rem 1rem;text-align:center;cursor:pointer;transition:border-color .15s,background .15s;background:#fafafa;}
.import-drop-zone:hover,.import-drop-zone.dragover{border-color:#ff6a00;background:rgba(255,106,0,0.03);}
.import-drop-icon{font-size:2rem;color:#9ca3af;display:block;margin-bottom:8px;}
.import-drop-text{font-size:0.9rem;color:#374151;margin:0 0 2px;}
.import-drop-sub{font-size:0.75rem;color:#9ca3af;}
.import-preview-box{background:#f9fafb;border:1px solid #e5e7eb;border-radius:10px;}
.import-file-icon{width:40px;height:40px;border-radius:8px;background:rgba(22,163,74,0.08);display:flex;align-items:center;justify-content:center;font-size:1.25rem;color:#16a34a;flex-shrink:0;}
.import-filename{font-size:0.85rem;font-weight:600;color:#111827;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;}
.import-filesize{font-size:0.75rem;color:#9ca3af;}
.import-progress-track{height:5px;border-radius:3px;background:#e5e7eb;overflow:hidden;}
.import-progress-fill{height:100%;border-radius:3px;background:linear-gradient(90deg,#ff6a00,#e05500);transition:width .35s ease;}
.import-success-banner{background:rgba(16,185,129,0.06);border:1px solid rgba(16,185,129,0.18);border-radius:10px;padding:10px 14px;font-size:0.82rem;color:#065f46;}
.import-error-banner{background:rgba(239,68,68,0.06);border:1px solid rgba(239,68,68,0.18);border-radius:10px;padding:10px 14px;font-size:0.82rem;color:#b91c1c;}
</style>

<script>
(function(){
    var _file = null;

    window.importHandleDrag = function(e, over) {
        e.preventDefault();
        document.getElementById('importDropZone').classList.toggle('dragover', over);
    };
    window.importHandleDrop = function(e) {
        e.preventDefault();
        document.getElementById('importDropZone').classList.remove('dragover');
        if (e.dataTransfer.files[0]) importShowPreview(e.dataTransfer.files[0]);
    };
    window.importHandleFile = function(inp) {
        if (inp.files[0]) importShowPreview(inp.files[0]);
    };

    function importShowPreview(file) {
        _file = file;
        document.getElementById('importFilename').textContent = file.name;
        document.getElementById('importFilesize').textContent = (file.size/1024).toFixed(1) + ' KB';
        document.getElementById('importPreview').style.display = 'block';
        document.getElementById('importDropZone').style.display = 'none';
        document.getElementById('importSubmitBtn').disabled = false;
        document.getElementById('importSuccess').style.display = 'none';
        document.getElementById('importError').style.display = 'none';
        document.getElementById('importProgress').style.display = 'none';
    }

    window.importClearFile = function() {
        _file = null;
        document.getElementById('importPreview').style.display = 'none';
        document.getElementById('importDropZone').style.display = 'block';
        document.getElementById('importSubmitBtn').disabled = true;
        document.getElementById('importFileInput').value = '';
    };

    window.importSubmit = function() {
        if (!_file) return;
        // Copy file to hidden form input via DataTransfer
        try {
            var dt = new DataTransfer();
            dt.items.add(_file);
            document.getElementById('importHiddenFile').files = dt.files;
        } catch(e) {
            // fallback: just submit with current input value
        }
        // Show progress animation
        document.getElementById('importSubmitBtn').disabled = true;
        document.getElementById('importProgress').style.display = 'block';
        var msgs = ['Parsing curriculum info…','Reading PLOs…','Reading subjects…','Saving…'];
        var step = 0;
        var fill = document.getElementById('importProgressFill');
        var msg  = document.getElementById('importProgressMsg');
        var pct  = document.getElementById('importProgressPct');
        var iv = setInterval(function(){
            step++;
            var p = Math.min(step * 22, 88);
            fill.style.width = p + '%';
            pct.textContent = p + '%';
            if (msgs[step]) msg.textContent = msgs[step];
            if (step >= 4) clearInterval(iv);
        }, 350);
        // Actually submit
        document.getElementById('importHiddenForm').submit();
    };

    // Reset modal state when closed
    document.addEventListener('DOMContentLoaded', function(){
        var modal = document.getElementById('importExcelModal');
        if (modal) {
            modal.addEventListener('hidden.bs.modal', function(){
                importClearFile();
            });
        }
    });
})();
</script>

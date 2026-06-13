<style>
    /* Font + base */
    * { font-family: 'Inter', sans-serif; }
    body { color: #1f2937; min-height: 100vh; background: linear-gradient(180deg,#eef6ff 0%, #f3f8ff 100%); }

    /* Theme variables */
    :root { --accent: #ff6a00; --accent-dark: #e05500; --muted: #6b7280; --card-bg: #ffffff; --border: rgba(15,23,42,0.06); }

    /* Sidebar */
    .sidebar {
        width: 260px; min-height: 100vh; position: fixed; left: 0; top: 0;
        background: #ffffff; border-right: 1px solid var(--border);
        padding: 1.5rem 0; z-index: 100;
    }
    .brand { padding: 0 1.5rem 1.5rem; font-size: 1.3rem; font-weight: 700;
              color: #111827; border-bottom: 1px solid var(--border); letter-spacing: -0.5px; }
    .brand span { color: var(--accent); }
    .nav-section { padding: 1rem 1rem 0.25rem; font-size: 0.7rem; font-weight: 600;
                    color: var(--muted); text-transform: uppercase; letter-spacing: 1px; }
    .nav-link { color: #374151; padding: 0.6rem 1.5rem; border-radius: 0; display: flex;
                align-items: center; gap: 0.75rem; font-size: 0.88rem; transition: all 0.15s; text-decoration: none; }
    .nav-link:hover, .nav-link.active {
        color: var(--accent-dark); background: rgba(255,106,0,0.04);
        border-left: 3px solid var(--accent); padding-left: calc(1.5rem - 3px);
    }

    /* Layout */
    .main-content { margin-left: 260px; padding: 2rem; background: transparent; }
    .topbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem; }
    .page-title { font-size: 1.5rem; font-weight: 700; color: #111827; }
    .page-subtitle { color: var(--muted); font-size: 0.85rem; }

    /* Cards / inputs */
    .card-dark {
        background: var(--card-bg);
        border-radius: 14px;
        padding: 18px;
        box-shadow: 0 12px 30px rgba(15,23,42,0.06);
        border: 1px solid transparent;
    }
    .search-bar, .form-control-dark, .form-select-dark, .form-control {
        background: #ffffff; border: 1px solid var(--border); border-radius: 10px;
        color: #111827; padding: 0.7rem 0.9rem; box-shadow: none;
    }
    .search-bar:focus, .form-control-dark:focus, .form-select-dark:focus, .form-control:focus {
        background: #ffffff; border-color: var(--accent);
        box-shadow: 0 8px 24px rgba(11,16,32,0.06); color: #111827;
    }
    .search-bar::placeholder, .form-control-dark::placeholder, .form-control::placeholder { color: #9ca3af; }

    /* Buttons */
    .btn-primary-custom {
        background: linear-gradient(135deg, var(--accent), var(--accent-dark));
        border: none; border-radius: 10px; color: #fff; padding: 0.7rem 1.15rem;
        font-weight: 700; font-size: 0.95rem; transition: all 0.12s; text-decoration: none; display: inline-block;
    }
    .btn-primary-custom:hover { transform: translateY(-2px); box-shadow: 0 12px 30px rgba(15,23,42,0.08); }
    .btn-secondary-custom {
        background: #ffffff; border: 1px solid var(--border); border-radius: 8px;
        color: #374151; padding: 0.55rem 1.15rem; font-size: 0.9rem; text-decoration: none; display: inline-block;
    }

    /* Tables */
    .table-dark-custom { --bs-table-bg: transparent; color: #111827; }
    .table-dark-custom thead th { background: #fbfbfb; color: var(--muted);
        font-size: 0.75rem; font-weight: 600; text-transform: uppercase;
        letter-spacing: 0.5px; border-color: var(--border); padding: 0.9rem; }
    .table-dark-custom tbody tr { border-color: var(--border); transition: background 0.12s; }
    .table-dark-custom tbody tr:hover { background: rgba(255,106,0,0.03); }
    .table-dark-custom td { padding: 0.8rem 0.9rem; vertical-align: middle; border-color: var(--border); }

    /* Badges */
    .badge-status { border-radius: 20px; padding: 0.3rem 0.75rem; font-size: 0.75rem; font-weight: 500; }
    .badge-draft    { background: rgba(107,114,128,0.08); color: #6b7280; }
    .badge-pending  { background: rgba(255,206,102,0.12);  color: #b45309; }
    .badge-approved { background: rgba(16,185,129,0.08);  color: #10b981; }
    .badge-archived, .badge-rejected { background: rgba(107,114,128,0.08); color: #6b7280; }

    .btn-action { border-radius: 8px; padding: 0.35rem 0.75rem; font-size: 0.85rem; font-weight: 600; border: none; transition: all 0.12s; text-decoration: none; display: inline-block; }
    .btn-view  { background: rgba(255,106,0,0.06); color: var(--accent-dark); }
    .btn-view:hover { background: rgba(255,106,0,0.09); }
    .btn-danger-custom { background: rgba(239,68,68,0.06); color: #ef4444; }
    .btn-success-custom { background: rgba(16,185,129,0.06); color: #10b981; }

    .alert-success-dark { background: rgba(16,185,129,0.06); border: 1px solid rgba(16,185,129,0.12); border-radius: 10px; color: #065f46; }
    .detail-label { color: var(--muted); font-size: 0.8rem; text-transform: uppercase; letter-spacing: 0.5px; }
    .detail-value { color: #111827; font-weight: 600; margin-bottom: 1rem; }
    option { background: #ffffff; color: #111827; }
    textarea.form-control-dark { min-height: 100px; }
</style>

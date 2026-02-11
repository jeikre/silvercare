<%@ page isELIgnored="true" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!doctype html>
<html>
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>SilverCare B2B Catalogue</title>
  <style>
    body{font-family:system-ui,Arial;margin:24px;background:#f6f8fb}
    .card{background:#fff;border:1px solid #e6e8ee;border-radius:14px;padding:16px;max-width:1100px;margin:0 auto}
    h1{margin:0 0 8px 0}
    .muted{color:#667085}
    .row{display:flex;gap:12px;flex-wrap:wrap;align-items:end;margin:14px 0}
    label{display:flex;flex-direction:column;gap:6px;font-size:13px}
    select,input{padding:10px 12px;border:1px solid #d0d5dd;border-radius:10px;min-width:260px}
    button{padding:10px 14px;border:1px solid #d0d5dd;border-radius:10px;background:#111827;color:#fff;cursor:pointer}
    button:disabled{opacity:.6;cursor:not-allowed}
    .grid{display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:12px;margin-top:14px}
    @media (max-width:900px){.grid{grid-template-columns:1fr}}
    .svc{border:1px solid #e6e8ee;border-radius:14px;padding:12px}
    .svc h3{margin:0 0 6px 0;font-size:16px}
    .pill{display:inline-block;padding:2px 8px;border-radius:999px;background:#eef2ff;color:#3730a3;font-size:12px}
    .err{color:#b42318;margin-top:10px}
    .topbar{display:flex;justify-content:space-between;align-items:center;gap:12px;flex-wrap:wrap}
    .small{font-size:12px}
  </style>
</head>
<body>
  <div class="card">
    <div class="topbar">
      <div>
        <h1>B2B Service Catalogue</h1>
        <div class="muted">Third-party portal consuming REST endpoints from CA2.</div>
      </div>
      <div class="small muted">
        API Base: <code id="apiBase"></code>
      </div>
    </div>

    <div class="row">
      <label>
        API Key (X-API-Key)
        <input id="apiKey" placeholder="e.g. CHANGE_ME_SECRET" value="CHANGE_ME_SECRET">
      </label>

      <label>
        Category
        <select id="categorySelect">
          <option value="">Loading categories…</option>
        </select>
      </label>

      <button id="loadBtn" disabled>Load Services</button>
    </div>

    <div id="status" class="muted"></div>
    <div id="error" class="err"></div>

    <div id="services" class="grid"></div>
  </div>

<script>
  // If your app is http://localhost:8080/CA2, then API is same origin.
  const API_BASE = `${location.origin}${location.pathname.split('/').slice(0,2).join('/')}`; 
  // Explanation: "/CA2/b2b/index.jsp" -> take "/CA2"
  document.getElementById("apiBase").textContent = API_BASE;

  const apiKeyEl = document.getElementById("apiKey");
  const categorySelect = document.getElementById("categorySelect");
  const loadBtn = document.getElementById("loadBtn");
  const servicesEl = document.getElementById("services");
  const statusEl = document.getElementById("status");
  const errorEl = document.getElementById("error");

  function setStatus(msg){ statusEl.textContent = msg || ""; }
  function setError(msg){ errorEl.textContent = msg || ""; }

  async function apiGet(path) {
    const key = apiKeyEl.value.trim();
    const res = await fetch(API_BASE + path, {
      headers: { "X-API-Key": key }
    });
    const text = await res.text();
    let json;
    try { json = JSON.parse(text); } catch { json = { raw: text }; }

    if (!res.ok) {
      const msg = json?.error ? `Error ${res.status}: ${json.error}` : `Error ${res.status}`;
      throw new Error(msg);
    }
    return json;
  }

  function money(x){
    if (x === null || x === undefined) return "—";
    const n = Number(x);
    if (Number.isNaN(n)) return String(x);
    return "$" + n.toFixed(2);
  }

  function renderServices(data){
    servicesEl.innerHTML = "";
    const list = data.services || [];
    if (!list.length) {
      servicesEl.innerHTML = `<div class="muted">No services found for this category.</div>`;
      return;
    }

    for (const s of list) {
      const div = document.createElement("div");
      div.className = "svc";
      div.innerHTML = `
        <div class="pill">Service ID: ${s.serviceId}</div>
        <h3>${s.serviceName ?? "Unnamed Service"}</h3>
        <div class="muted">${s.serviceDescription ?? ""}</div>
        <div style="margin-top:10px;display:flex;gap:10px;flex-wrap:wrap;align-items:center">
          <strong>${money(s.price)}</strong>
          <span class="muted">Duration: ${s.duration ?? "—"}</span>
          <span class="muted">Caregiver: ${s.caregiverName ?? "—"}</span>
        </div>
      `;
      servicesEl.appendChild(div);
    }
  }

  async function loadCategories(){
    setError("");
    setStatus("Loading categories…");
    categorySelect.innerHTML = `<option value="">Loading categories…</option>`;
    loadBtn.disabled = true;

    try {
      const data = await apiGet("/api/b2b/categories");
      const cats = data.categories || [];
      categorySelect.innerHTML = `<option value="">-- Select a category --</option>`;
      for (const c of cats) {
        const opt = document.createElement("option");
        opt.value = c.categoryId;
        opt.textContent = `${c.categoryName} (ID ${c.categoryId})`;
        categorySelect.appendChild(opt);
      }
      setStatus(`Loaded ${cats.length} categories.`);
    } catch (e) {
      setError(e.message);
      setStatus("");
      categorySelect.innerHTML = `<option value="">Failed to load</option>`;
    }
  }

  async function loadServices(){
    setError("");
    servicesEl.innerHTML = "";
    const catId = categorySelect.value;
    if (!catId) {
      setError("Please select a category.");
      return;
    }

    try {
      setStatus("Loading services…");
      const data = await apiGet(`/api/b2b/services?categoryId=${encodeURIComponent(catId)}`);
      setStatus(`Loaded ${data.count ?? 0} services.`);
      renderServices(data);
    } catch (e) {
      setError(e.message);
      setStatus("");
    }
  }

  categorySelect.addEventListener("change", () => {
    loadBtn.disabled = !categorySelect.value;
  });
  loadBtn.addEventListener("click", loadServices);

  // init
  loadCategories();
</script>
</body>
</html>

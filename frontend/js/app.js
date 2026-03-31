(function () {
  const views = {
    login: document.getElementById("viewLogin"),
    register: document.getElementById("viewRegister"),
    dashboard: document.getElementById("viewDashboard"),
    builder: document.getElementById("viewBuilder"),
    preview: document.getElementById("viewPreview"),
    shared: document.getElementById("viewShared")
  };

  const darkBtn = document.getElementById("darkModeBtn");
  const logoutBtn = document.getElementById("logoutBtn");

  const ResumePreviewFinalEl = document.getElementById("resumePreviewFinal");
  const ResumeSharedFinalEl = document.getElementById("resumeSharedFinal");
  const shareResultEl = document.getElementById("shareResult");
  const previewErrorEl = document.getElementById("previewError");

  const resumeListEl = document.getElementById("resumeList");
  const createResumeModalEl = document.getElementById("createResumeModal");
  const createResumeFormEl = document.getElementById("createResumeForm");

  const btnCloseCreateModal = document.getElementById("btnCloseCreateModal");
  const createResumeErrorEl = document.getElementById("createResumeError");

  let currentResumeId = null;

  function hideAll() {
    Object.values(views).forEach(v => v.style.display = "none");
  }

  function show(viewName) {
    hideAll();
    views[viewName].style.display = "";
  }

  function setTheme(theme) {
    // CSS expects body.light for light theme.
    document.body.classList.toggle("light", theme === "light");
    localStorage.setItem("resumeBuilderTheme", theme);
  }

  function initTheme() {
    const saved = localStorage.getItem("resumeBuilderTheme") || "dark";
    setTheme(saved);
  }

  function getRoute() {
    const hash = (location.hash || "#/login").replace(/^#/, "");
    const parts = hash.split("/").filter(Boolean);
    return parts;
  }

  function requireAuthOrRedirect() {
    const token = window.Auth.getToken();
    if (!token) {
      location.hash = "#/login";
      return false;
    }
    logoutBtn.style.display = "";
    return true;
  }

  async function loadDashboard() {
    if (!requireAuthOrRedirect()) return;
    show("dashboard");
    resumeListEl.innerHTML = "Loading...";

    try {
      const payload = await window.Auth.apiFetch("/api/resumes?page=0&size=50");
      const resumes = payload.content || [];
      resumeListEl.innerHTML = "";

      if (!resumes.length) {
        resumeListEl.innerHTML = `<div class="mutedSmall">No resumes yet. Create one.</div>`;
        return;
      }

      resumes.forEach(r => {
        const item = document.createElement("div");
        item.className = "resumeItem";
        item.innerHTML = `
          <div>
            <strong>${escapeHtml(r.fullName || '')}</strong>
            <div class="resumeMeta">Template: ${escapeHtml(r.templateKey || '')} • Updated: ${escapeHtml(formatDate(r.updatedAt))}</div>
          </div>
          <div class="resumeBtns">
            <button class="btn btnGhost" type="button" data-action="preview" data-id="${r.id}">
              <i class="fa-solid fa-eye"></i> Preview
            </button>
            <button class="btn btnPrimary" type="button" data-action="edit" data-id="${r.id}">
              <i class="fa-solid fa-pen-to-square"></i> Edit
            </button>
            <button class="btn btnGhost" type="button" data-action="delete" data-id="${r.id}">
              <i class="fa-solid fa-trash"></i> Delete
            </button>
          </div>
        `;

        item.querySelectorAll("button").forEach(b => {
          b.addEventListener("click", async (e) => {
            const id = Number(b.getAttribute("data-id"));
            const action = b.getAttribute("data-action");
            if (action === "preview") location.hash = `#/preview/${id}`;
            if (action === "edit") location.hash = `#/builder/${id}`;
            if (action === "delete") await deleteResume(id);
          });
        });

        resumeListEl.appendChild(item);
      });
    } catch (err) {
      resumeListEl.innerHTML = "";
      alert(err.message || "Failed to load resumes");
    }
  }

  async function deleteResume(id) {
    if (!confirm("Delete this resume?")) return;
    try {
      await window.Auth.apiFetch(`/api/resumes/${id}`, { method: "DELETE" });
      await loadDashboard();
    } catch (err) {
      alert(err.message || "Delete failed");
    }
  }

  async function loadBuilder(resumeId) {
    if (!requireAuthOrRedirect()) return;
    show("builder");
    currentResumeId = Number(resumeId);

    try {
      await window.ResumeBuilder.load(currentResumeId);
      document.getElementById("btnSaveResume").onclick = async () => {
        try {
          await window.ResumeBuilder.save();
        } catch (err) {
          const box = document.getElementById("builderError");
          box.style.display = "block";
          if (!box.textContent || !box.textContent.trim()) {
            box.textContent = err.message || "Save failed";
          }
        }
      };

      document.getElementById("btnScoreResume").onclick = async () => {
        try {
          const scoreRes = await window.ResumeBuilder.score();
          const box = document.getElementById("scoreBox");
          const value = document.getElementById("scoreValue");
          const ul = document.getElementById("scoreSuggestions");
          box.style.display = "";
          value.textContent = String(scoreRes.score || 0);
          ul.innerHTML = "";
          (scoreRes.suggestions || []).forEach(s => {
            const li = document.createElement("li");
            li.textContent = s;
            ul.appendChild(li);
          });
        } catch (err) {
          alert(err.message || "Score failed");
        }
      };

      document.getElementById("btnPreviewResume").onclick = () => {
        location.hash = `#/preview/${currentResumeId}`;
      };

      // Default template select is bound inside ResumeBuilder.init
    } catch (err) {
      show("dashboard");
      alert(err.message || "Failed to load builder");
    }
  }

  async function loadPreview(resumeId) {
    if (!requireAuthOrRedirect()) {
      // preview may still work if token missing; but dashboard expects auth.
      // We'll redirect to login for safety.
      location.hash = "#/login";
      return;
    }

    show("preview");
    currentResumeId = Number(resumeId);
    previewErrorEl.style.display = "none";
    shareResultEl.style.display = "none";
    shareResultEl.textContent = "";

    try {
      const resume = await window.Auth.apiFetch(`/api/resumes/${currentResumeId}`);
      const templateKey = resume.templateKey || "classic";
      const renderer = window.ResumeTemplates[templateKey].render;
      ResumePreviewFinalEl.innerHTML = renderer(resume);

      document.getElementById("btnDownloadPdf").onclick = async () => {
        const fullName = resume.fullName ? resume.fullName.replace(/\s+/g, "_") : "resume";
        await window.PdfExporter.downloadPdf(ResumePreviewFinalEl, fullName);
      };

      document.getElementById("btnShareResume").onclick = async () => {
        try {
          const res = await window.Auth.apiFetch(`/api/resumes/${currentResumeId}/share`, {
            method: "POST"
          });
          const url = res.shareUrl;
          shareResultEl.style.display = "";
          shareResultEl.textContent = `Share link ready.`;
          await tryCopy(url);
        } catch (err) {
          previewErrorEl.style.display = "block";
          previewErrorEl.textContent = err.message || "Share failed";
        }
      };

      document.getElementById("btnEditResume").onclick = () => {
        location.hash = `#/builder/${currentResumeId}`;
      };
    } catch (err) {
      previewErrorEl.style.display = "block";
      previewErrorEl.textContent = err.message || "Failed to load preview";
    }
  }

  async function loadShared(shareToken) {
    show("shared");
    try {
      const resume = await fetchShared(sharedTokenUrl(shareToken));
      const templateKey = resume.templateKey || "classic";
      ResumeSharedFinalEl.innerHTML = window.ResumeTemplates[templateKey].render(resume);

      document.getElementById("btnBackHomeShared").onclick = () => {
        location.hash = window.Auth.getToken() ? "#/dashboard" : "#/login";
      };
    } catch (err) {
      alert(err.message || "Failed to load shared resume");
      location.hash = "#/login";
    }
  }

  function sharedTokenUrl(token) {
    return `/api/resumes/shared/${encodeURIComponent(token)}`;
  }

  async function fetchShared(path) {
    const base = window.Auth.getApiBase();
    const res = await fetch(base + path);
    if (!res.ok) throw new Error(`Failed (${res.status})`);
    return res.json();
  }

  function wireAuthForms() {
    const loginForm = document.getElementById("loginForm");
    const loginError = document.getElementById("loginError");
    loginForm.addEventListener("submit", async (e) => {
      e.preventDefault();
      loginError.style.display = "none";
      try {
        const email = document.getElementById("loginEmail").value;
        const password = document.getElementById("loginPassword").value;
        await window.Auth.login(email, password);
        location.hash = "#/dashboard";
      } catch (err) {
        loginError.style.display = "block";
        loginError.textContent = err.message || "Login failed";
      }
    });

    const registerForm = document.getElementById("registerForm");
    const registerError = document.getElementById("registerError");
    registerForm.addEventListener("submit", async (e) => {
      e.preventDefault();
      registerError.style.display = "none";
      try {
        const fullName = document.getElementById("registerFullName").value;
        const email = document.getElementById("registerEmail").value;
        const password = document.getElementById("registerPassword").value;
        await window.Auth.register(fullName, email, password);
        location.hash = "#/dashboard";
      } catch (err) {
        registerError.style.display = "block";
        registerError.textContent = err.message || "Register failed";
      }
    });
  }

  function wireCreateResume() {
    document.getElementById("btnCreateResume").addEventListener("click", () => {
      createResumeErrorEl.style.display = "none";
      createResumeModalEl.style.display = "flex";
    });
    btnCloseCreateModal.addEventListener("click", () => {
      createResumeModalEl.style.display = "none";
    });

    createResumeFormEl.addEventListener("submit", async (e) => {
      e.preventDefault();
      createResumeErrorEl.style.display = "none";
      try {
        const fullName = document.getElementById("createFullName").value;
        const templateKey = document.getElementById("createTemplateKey").value;
        const headline = document.getElementById("createHeadline").value;
        const res = await window.Auth.apiFetch("/api/resumes", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ fullName, templateKey, headline })
        });
        createResumeModalEl.style.display = "none";
        await loadBuilder(res.id);
      } catch (err) {
        createResumeErrorEl.style.display = "block";
        createResumeErrorEl.textContent = err.message || "Create failed";
      }
    });
  }

  function escapeHtml(str) {
    return String(str ?? '')
      .replaceAll("&", "&amp;")
      .replaceAll("<", "&lt;")
      .replaceAll(">", "&gt;")
      .replaceAll('"', "&quot;");
  }

  function formatDate(val) {
    if (!val) return "";
    try {
      return new Date(val).toLocaleDateString();
    } catch (_) {
      return val;
    }
  }

  async function tryCopy(text) {
    if (!text) return;
    try {
      await navigator.clipboard.writeText(text);
    } catch (_) {
      // Clipboard might not work in some browsers.
      prompt("Copy share link:", text);
    }
  }

  function route() {
    const [root, param] = getRoute();
    if (!root) return show("login");

    if (root === "login") return show("login"), Promise.resolve();
    if (root === "register") return show("register"), Promise.resolve();

    if (root === "dashboard") return loadDashboard();
    if (root === "builder") return loadBuilder(param);
    if (root === "preview") return loadPreview(param);

    if (root === "share") return loadShared(param);
    show("login");
  }

  async function bootstrap() {
    initTheme();
    wireAuthForms();
    wireCreateResume();

    darkBtn.addEventListener("click", () => {
      const now = localStorage.getItem("resumeBuilderTheme") || "dark";
      setTheme(now === "dark" ? "light" : "dark");
    });

    logoutBtn.addEventListener("click", () => {
      window.Auth.clearToken();
      location.hash = "#/login";
    });

    window.addEventListener("hashchange", route);

    // Initial route load.
    const parts = getRoute();
    if (parts[0] === "dashboard" && window.Auth.getToken()) logoutBtn.style.display = "";
    route();
  }

  bootstrap();
})();


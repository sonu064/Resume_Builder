window.ResumeBuilder = window.ResumeBuilder || {};

(function () {
  const api = () => window.Auth.apiFetch;
  let resumeId = null;
  let draft = null;
  let activeStep = 1;
  let templatePickerBound = false;

  const el = {
    step1: document.getElementById("builderStep1"),
    step2: document.getElementById("builderStep2"),
    step3: document.getElementById("builderStep3"),
    step4: document.getElementById("builderStep4"),
    step5: document.getElementById("builderStep5"),
    preview: document.getElementById("resumePreview"),
    templateSelect: document.getElementById("templateKeySelect"),
    templatePicker: document.getElementById("templateCardPicker"),
    error: document.getElementById("builderError"),
    success: document.getElementById("builderSuccess"),
    scoreBox: document.getElementById("scoreBox"),
    scoreValue: document.getElementById("scoreValue"),
    scoreSuggestions: document.getElementById("scoreSuggestions"),
    scoreBtn: document.getElementById("btnScoreResume"),
    saveBtn: document.getElementById("btnSaveResume"),
  };

  function init(resumeIdArg, existingDraft) {
    resumeId = Number(resumeIdArg);
    draft = existingDraft;
    if (!draft.education) draft.education = [];
    if (!draft.experience) draft.experience = [];
    if (!draft.skills) draft.skills = [];
    if (!draft.projects) draft.projects = [];

    buildSteps();

    // Template selector drives both builder preview and save payload.
    syncTemplateSelection(draft.templateKey || "classic");
    bindTemplatePicker();

    fillStep1();
    renderListSection(el.step2, draft.education, renderEducationItem, onEducationChanged, () => ({
      degree: "",
      school: "",
      location: "",
      startDate: null,
      endDate: null,
      description: ""
    }));
    renderListSection(el.step3, draft.experience, renderExperienceItem, onExperienceChanged, () => ({
      role: "",
      company: "",
      description: "",
      startDate: null,
      endDate: null
    }));
    renderListSection(el.step4, draft.skills, renderSkillItem, onSkillsChanged, () => ({
      name: "",
      level: ""
    }));
    renderListSection(el.step5, draft.projects, renderProjectItem, onProjectsChanged, () => ({
      name: "",
      url: "",
      description: "",
      techStack: ""
    }));

    wireStepButtons();
    wireStepNav();
    setStep(1);

    renderPreview();
  }

  function buildSteps() {
    el.step1.innerHTML = `
      <div class="formGrid">
        <div style="grid-column: 1 / -1;">
          <label>Full name</label>
          <input id="b_fullName" type="text" value="${esc(draft.fullName || '')}" required />
        </div>

        <div style="grid-column: 1 / -1;">
          <label>Profile image (optional)</label>
          <input id="b_profileImageFile" type="file" accept="image/*" />
          <div style="display:flex;gap:12px;flex-wrap:wrap;align-items:center;margin-top:10px;">
            <button class="btn btnGhost" type="button" id="btnUploadProfileImage">
              <i class="fa-solid fa-upload"></i> Upload
            </button>
            <div id="b_profileImagePreviewWrap" style="display:flex;align-items:center;gap:12px;">
              <img id="b_profileImagePreview" class="profileImg" style="display:${draft.profileImageUrl ? "" : "none"};" alt="Profile preview" src="${draft.profileImageUrl ? escapeForAttr(draft.profileImageUrl) : ""}" />
              <span class="mutedSmall">Used in templates.</span>
            </div>
          </div>
        </div>

        <div style="grid-column: 1 / -1;">
          <label>Headline</label>
          <input id="b_headline" type="text" value="${esc(draft.headline || '')}" />
        </div>

        <div>
          <label>Email</label>
          <input id="b_email" type="email" value="${esc(draft.email || '')}" />
        </div>
        <div>
          <label>Phone</label>
          <input id="b_phone" type="text" value="${esc(draft.phone || '')}" />
        </div>

        <div>
          <label>Website</label>
          <input id="b_website" type="text" value="${esc(draft.website || '')}" />
        </div>
        <div>
          <label>Location</label>
          <input id="b_location" type="text" value="${esc(draft.location || '')}" />
        </div>

        <div style="grid-column: 1 / -1;">
          <label>Summary</label>
          <textarea id="b_summary">${esc(draft.summary || '')}</textarea>
        </div>
      </div>
      <div class="formRow">
        <button class="btn btnGhost" type="button" id="btnAddEducation">
          <i class="fa-solid fa-graduation-cap"></i> Add Education
        </button>
      </div>
    `;

    document.getElementById("b_fullName").addEventListener("input", (e) => {
      draft.fullName = e.target.value;
      renderPreview();
    });
    document.getElementById("b_headline").addEventListener("input", (e) => {
      draft.headline = e.target.value;
      renderPreview();
    });
    document.getElementById("b_email").addEventListener("input", (e) => {
      draft.email = e.target.value;
      renderPreview();
    });
    document.getElementById("b_phone").addEventListener("input", (e) => {
      draft.phone = e.target.value;
      renderPreview();
    });
    document.getElementById("b_website").addEventListener("input", (e) => {
      draft.website = e.target.value;
      renderPreview();
    });
    document.getElementById("b_location").addEventListener("input", (e) => {
      draft.location = e.target.value;
      renderPreview();
    });
    document.getElementById("b_summary").addEventListener("input", (e) => {
      draft.summary = e.target.value;
      renderPreview();
    });

    const fileInput = document.getElementById("b_profileImageFile");
    const previewImg = document.getElementById("b_profileImagePreview");
    const uploadBtn = document.getElementById("btnUploadProfileImage");

    fileInput.addEventListener("change", () => {
      const file = fileInput.files && fileInput.files[0];
      if (!file) return;
      previewImg.style.display = "";
      previewImg.src = URL.createObjectURL(file);
    });

    uploadBtn.addEventListener("click", async () => {
      const file = fileInput.files && fileInput.files[0];
      if (!file) {
        showError("Choose an image file first.");
        return;
      }

      try {
        const base = window.Auth.getApiBase();
        const token = window.Auth.getToken();
        const form = new FormData();
        form.append("image", file);

        const res = await fetch(base + "/api/users/me/profile-image", {
          method: "POST",
          headers: token ? { Authorization: `Bearer ${token}` } : {},
          body: form
        });

        let payload = null;
        try { payload = await res.json(); } catch (_) {}
        if (!res.ok) {
          const msg = payload && payload.message ? payload.message : `Upload failed (${res.status})`;
          throw new Error(msg);
        }

        draft.profileImageUrl = payload.profileImageUrl;
        previewImg.src = draft.profileImageUrl;
        renderPreview();
        showSuccess("Profile image updated.");
        setTimeout(() => { el.success.style.display = "none"; }, 2500);
      } catch (err) {
        showError(err.message || "Upload failed");
      }
    });
  }

  function fillStep1() {
    // step 1 already populated from draft in buildSteps.
  }

  function wireStepButtons() {
    const stepBtns = document.querySelectorAll(".stepBtn");
    stepBtns.forEach(btn => {
      btn.addEventListener("click", () => {
        const s = Number(btn.getAttribute("data-step"));
        setStep(s);
      });
    });
  }

  function wireStepNav() {
    document.getElementById("btnBackStep").addEventListener("click", () => {
      setStep(Math.max(1, activeStep - 1));
    });
    document.getElementById("btnNextStep").addEventListener("click", () => {
      setStep(Math.min(5, activeStep + 1));
    });
  }

  function setStep(step) {
    activeStep = step;
    // Toggle step panes
    el.step1.style.display = step === 1 ? "" : "none";
    el.step2.style.display = step === 2 ? "" : "none";
    el.step3.style.display = step === 3 ? "" : "none";
    el.step4.style.display = step === 4 ? "" : "none";
    el.step5.style.display = step === 5 ? "" : "none";

    document.querySelectorAll(".stepBtn").forEach(b => {
      const s = Number(b.getAttribute("data-step"));
      b.classList.toggle("active", s === step);
    });
  }

  function showError(msg) {
    el.error.textContent = msg;
    el.error.style.display = "block";
  }

  function showSuccess(msg) {
    el.success.textContent = msg;
    el.success.style.display = "block";
  }

  function renderPreview() {
    const templateKey = el.templateSelect.value;
    draft.templateKey = templateKey;

    const renderer = window.ResumeTemplates[templateKey] && window.ResumeTemplates[templateKey].render;
    if (!renderer) return;

    el.preview.innerHTML = renderer(draft);
  }

  function bindTemplatePicker() {
    if (templatePickerBound) return;
    templatePickerBound = true;

    el.templateSelect.addEventListener("change", () => {
      syncTemplateSelection(el.templateSelect.value);
      renderPreview();
    });

    if (!el.templatePicker) return;
    const inputs = Array.from(el.templatePicker.querySelectorAll(".templateCardInput"));
    inputs.forEach((input) => {
      input.addEventListener("change", () => {
        syncTemplateSelection(input.value);
        renderPreview();
      });
    });
  }

  function syncTemplateSelection(key) {
    const selectedKey = key || "classic";
    el.templateSelect.value = selectedKey;
    draft.templateKey = selectedKey;

    if (!el.templatePicker) return;
    const options = Array.from(el.templatePicker.querySelectorAll(".templateCardOption"));
    options.forEach((btn) => {
      const isActive = btn.dataset.templateKey === selectedKey;
      btn.classList.toggle("active", isActive);
    });

    const inputs = Array.from(el.templatePicker.querySelectorAll(".templateCardInput"));
    inputs.forEach((input) => {
      input.checked = input.value === selectedKey;
    });
  }

  function renderListSection(containerEl, items, itemRenderer, onChanged, newItemFactory) {
    containerEl.innerHTML = `
      <div class="formRow" style="display:flex;justify-content:space-between;gap:12px;flex-wrap:wrap;align-items:center;">
        <div class="mutedSmall">Drag & drop ordering can be added later. Current order is the list order.</div>
        <button class="btn btnGhost" type="button" id="btnAddItem">
          <i class="fa-solid fa-plus"></i> Add
        </button>
      </div>
      <div id="listItems" style="display:flex;flex-direction:column;gap:12px;margin-top:12px;"></div>
    `;

    const list = containerEl.querySelector("#listItems");
    list.innerHTML = "";

    items.forEach((item, idx) => {
      const itemEl = itemRenderer(item, idx);
      list.appendChild(itemEl);
    });

    // Drag-and-drop reordering (bonus).
    let dragIndex = null;
    Array.from(list.children).forEach((child, idx) => {
      child.setAttribute("draggable", "true");
      child.dataset.index = String(idx);

      child.addEventListener("dragstart", (e) => {
        dragIndex = idx;
        e.dataTransfer.effectAllowed = "move";
        try { e.dataTransfer.setData("text/plain", String(idx)); } catch (_) {}
      });

      child.addEventListener("dragover", (e) => {
        e.preventDefault();
      });

      child.addEventListener("drop", (e) => {
        e.preventDefault();
        if (dragIndex === null || dragIndex === idx) return;

        const moved = items.splice(dragIndex, 1)[0];
        items.splice(idx, 0, moved);
        dragIndex = null;
        onChanged();
      });
    });

    containerEl.querySelector("#btnAddItem").addEventListener("click", () => {
      items.push(newItemFactory ? newItemFactory() : {});
      onChanged();
    });
  }

  function onEducationChanged() {
    // Ensure items exist.
    draft.education = draft.education || [];
    renderPreview();
    // Re-render step UI to include new item fields.
    renderListSection(el.step2, draft.education, renderEducationItem, onEducationChanged, () => ({
      degree: "",
      school: "",
      location: "",
      startDate: null,
      endDate: null,
      description: ""
    }));
  }

  function onExperienceChanged() {
    draft.experience = draft.experience || [];
    renderPreview();
    renderListSection(el.step3, draft.experience, renderExperienceItem, onExperienceChanged, () => ({
      role: "",
      company: "",
      description: "",
      startDate: null,
      endDate: null
    }));
  }

  function onSkillsChanged() {
    draft.skills = draft.skills || [];
    renderPreview();
    renderListSection(el.step4, draft.skills, renderSkillItem, onSkillsChanged, () => ({
      name: "",
      level: ""
    }));
  }

  function onProjectsChanged() {
    draft.projects = draft.projects || [];
    renderPreview();
    renderListSection(el.step5, draft.projects, renderProjectItem, onProjectsChanged, () => ({
      name: "",
      url: "",
      description: "",
      techStack: ""
    }));
  }

  function renderEducationItem(item, idx) {
    const wrap = document.createElement("div");
    wrap.className = "cardSoft";
    wrap.innerHTML = `
      <div class="formGrid">
        <div style="grid-column:1/-1;">
          <div style="display:flex;justify-content:space-between;gap:12px;align-items:center;">
            <strong>Education #${idx + 1}</strong>
            <button type="button" class="btn btnGhost" data-remove="education" data-index="${idx}">
              <i class="fa-solid fa-trash"></i> Remove
            </button>
          </div>
        </div>
        <div style="grid-column:1/-1;"><label>Degree</label><input data-field="degree" type="text" value="${esc(item.degree || '')}" /></div>
        <div style="grid-column:1/-1;"><label>School</label><input data-field="school" type="text" value="${esc(item.school || '')}" /></div>
        <div><label>Start date</label><input data-field="startDate" type="date" value="${dateVal(item.startDate)}" /></div>
        <div><label>End date</label><input data-field="endDate" type="date" value="${dateVal(item.endDate)}" /></div>
        <div style="grid-column:1/-1;"><label>Location</label><input data-field="location" type="text" value="${esc(item.location || '')}" /></div>
        <div style="grid-column:1/-1;"><label>Description</label><textarea data-field="description">${esc(item.description || '')}</textarea></div>
      </div>
    `;

    bindFields(wrap, item, idx, (newItem) => { draft.education[idx] = newItem; renderPreview(); });

    wrap.querySelector('[data-remove="education"]').addEventListener("click", (e) => {
      const i = Number(e.currentTarget.getAttribute("data-index"));
      draft.education.splice(i, 1);
      onEducationChanged();
    });

    return wrap;
  }

  function renderExperienceItem(item, idx) {
    const wrap = document.createElement("div");
    wrap.className = "cardSoft";
    wrap.innerHTML = `
      <div class="formGrid">
        <div style="grid-column:1/-1;">
          <div style="display:flex;justify-content:space-between;gap:12px;align-items:center;">
            <strong>Experience #${idx + 1}</strong>
            <button type="button" class="btn btnGhost" data-remove="experience" data-index="${idx}">
              <i class="fa-solid fa-trash"></i> Remove
            </button>
          </div>
        </div>
        <div style="grid-column:1/-1;"><label>Role</label><input data-field="role" type="text" value="${esc(item.role || '')}" /></div>
        <div style="grid-column:1/-1;"><label>Company</label><input data-field="company" type="text" value="${esc(item.company || '')}" /></div>
        <div><label>Start date</label><input data-field="startDate" type="date" value="${dateVal(item.startDate)}" /></div>
        <div><label>End date</label><input data-field="endDate" type="date" value="${dateVal(item.endDate)}" /></div>
        <div style="grid-column:1/-1;"><label>Description</label><textarea data-field="description">${esc(item.description || '')}</textarea></div>
      </div>
    `;

    bindFields(wrap, item, idx, (newItem) => { draft.experience[idx] = newItem; renderPreview(); });

    wrap.querySelector('[data-remove="experience"]').addEventListener("click", (e) => {
      const i = Number(e.currentTarget.getAttribute("data-index"));
      draft.experience.splice(i, 1);
      onExperienceChanged();
    });

    return wrap;
  }

  function renderSkillItem(item, idx) {
    const wrap = document.createElement("div");
    wrap.className = "cardSoft";
    wrap.innerHTML = `
      <div class="formGrid">
        <div style="grid-column:1/-1;">
          <div style="display:flex;justify-content:space-between;gap:12px;align-items:center;">
            <strong>Skill #${idx + 1}</strong>
            <button type="button" class="btn btnGhost" data-remove="skills" data-index="${idx}">
              <i class="fa-solid fa-trash"></i> Remove
            </button>
          </div>
        </div>
        <div style="grid-column:1/-1;"><label>Name</label><input data-field="name" type="text" value="${esc(item.name || '')}" /></div>
        <div style="grid-column:1/-1;"><label>Level (optional)</label><input data-field="level" type="text" value="${esc(item.level || '')}" /></div>
      </div>
    `;

    bindFields(wrap, item, idx, (newItem) => { draft.skills[idx] = newItem; renderPreview(); });

    wrap.querySelector('[data-remove="skills"]').addEventListener("click", (e) => {
      const i = Number(e.currentTarget.getAttribute("data-index"));
      draft.skills.splice(i, 1);
      onSkillsChanged();
    });

    return wrap;
  }

  function renderProjectItem(item, idx) {
    const wrap = document.createElement("div");
    wrap.className = "cardSoft";
    wrap.innerHTML = `
      <div class="formGrid">
        <div style="grid-column:1/-1;">
          <div style="display:flex;justify-content:space-between;gap:12px;align-items:center;">
            <strong>Project #${idx + 1}</strong>
            <button type="button" class="btn btnGhost" data-remove="projects" data-index="${idx}">
              <i class="fa-solid fa-trash"></i> Remove
            </button>
          </div>
        </div>
        <div style="grid-column:1/-1;"><label>Name</label><input data-field="name" type="text" value="${esc(item.name || '')}" /></div>
        <div style="grid-column:1/-1;"><label>URL (optional)</label><input data-field="url" type="text" value="${esc(item.url || '')}" /></div>
        <div style="grid-column:1/-1;"><label>Tech stack (optional)</label><input data-field="techStack" type="text" value="${esc(item.techStack || '')}" /></div>
        <div style="grid-column:1/-1;"><label>Description</label><textarea data-field="description">${esc(item.description || '')}</textarea></div>
      </div>
    `;

    bindFields(wrap, item, idx, (newItem) => { draft.projects[idx] = newItem; renderPreview(); });

    wrap.querySelector('[data-remove="projects"]').addEventListener("click", (e) => {
      const i = Number(e.currentTarget.getAttribute("data-index"));
      draft.projects.splice(i, 1);
      onProjectsChanged();
    });

    return wrap;
  }

  function bindFields(container, item, idx, onUpdate) {
    container.querySelectorAll("[data-field]").forEach(input => {
      input.addEventListener("input", () => {
        const field = input.getAttribute("data-field");
        let value = input.value;
        // Date inputs return yyyy-mm-dd or empty string.
        if (input.type === "date") value = value || null;
        item[field] = value;
        onUpdate(item);
      });
    });
  }

  function dateVal(v) {
    if (!v) return "";
    // JSON from backend often is "YYYY-MM-DD"
    return String(v).slice(0, 10);
  }

  function esc(str) {
    return String(str ?? "").replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll('"', "&quot;");
  }

  function escapeForAttr(str) {
    return esc(str).replaceAll("'", "&#039;");
  }

  async function load(resumeIdArg) {
    resumeId = Number(resumeIdArg);
    const data = await api()(`/api/resumes/${resumeId}`);
    init(resumeId, data);
  }

  async function save() {
    const payload = buildSavePayload();
    el.error.style.display = "none";
    el.success.style.display = "none";

    // Basic client-side guard rails to avoid round trips.
    if (!draft.fullName || String(draft.fullName).trim().length < 1) {
      showError("Full name is required.");
      throw new Error("Full name is required.");
    }

    try {
      const saved = await api()(`/api/resumes/${resumeId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      });

      draft = saved;
      // Keep the UI in sync even if backend normalizes ordering.
      init(resumeId, draft);
      showSuccess("Resume saved successfully.");
      setTimeout(() => { el.success.style.display = "none"; }, 2500);
      return saved;
    } catch (err) {
      showError(extractApiErrorMessage(err));
      throw err;
    }
  }

  function buildSavePayload() {
    // Backend expects list wrappers; we send arrays directly to list request types.
    // We keep ordering as list order; positions are set server-side.
    return {
      fullName: draft.fullName || "",
      headline: draft.headline || "",
      email: draft.email || null,
      phone: draft.phone || null,
      website: draft.website || null,
      location: draft.location || null,
      summary: draft.summary || null,
      templateKey: el.templateSelect.value || "classic",
      education: normalizeEducation(draft.education),
      experience: normalizeExperience(draft.experience),
      skills: normalizeSkills(draft.skills),
      projects: normalizeProjects(draft.projects)
    };
  }

  function normalizeEducation(items) {
    const normalized = (items || []).map((it, idx) => ({
      position: it.position ?? idx,
      degree: it.degree || "",
      school: it.school || "",
      location: it.location || "",
      startDate: it.startDate || null,
      endDate: it.endDate || null,
      description: it.description || ""
    }));
    return normalized
      .filter(x => (x.degree || "").trim().length > 0 && (x.school || "").trim().length > 0)
      .map((x, idx) => ({ ...x, position: idx }));
  }

  function normalizeExperience(items) {
    const normalized = (items || []).map((it, idx) => ({
      position: it.position ?? idx,
      role: it.role || "",
      company: it.company || "",
      description: it.description || "",
      startDate: it.startDate || null,
      endDate: it.endDate || null
    }));
    return normalized
      .filter(x => (x.role || "").trim().length > 0 && (x.company || "").trim().length > 0)
      .map((x, idx) => ({ ...x, position: idx }));
  }

  function normalizeSkills(items) {
    const normalized = (items || []).map((it, idx) => ({
      position: it.position ?? idx,
      name: it.name || "",
      level: it.level || null
    }));
    return normalized
      .filter(x => (x.name || "").trim().length > 0)
      .map((x, idx) => ({ ...x, position: idx }));
  }

  function normalizeProjects(items) {
    const normalized = (items || []).map((it, idx) => ({
      position: it.position ?? idx,
      name: it.name || "",
      url: it.url || null,
      description: it.description || "",
      techStack: it.techStack || null
    }));
    return normalized
      .filter(x => (x.name || "").trim().length > 0)
      .map((x, idx) => ({ ...x, position: idx }));
  }

  async function score() {
    const res = await api()(`/api/resumes/${resumeId}/score`, {
      method: "POST",
      headers: { "Content-Type": "application/json" }
    });
    return res;
  }

  function extractApiErrorMessage(err) {
    if (!err) return "Request failed";

    const payload = err.payload;
    if (payload && payload.details && Array.isArray(payload.details.fieldErrors) && payload.details.fieldErrors.length) {
      // Show a compact summary (top 5 fields).
      return payload.details.fieldErrors.slice(0, 5).map(fe => `${fe.field}: ${fe.message}`).join(" | ");
    }

    return err.message || "Request failed";
  }

  // Expose to app.js
  window.ResumeBuilder = {
    load,
    init,
    renderPreview,
    save,
    score,
    getDraft: () => draft
  };
})();


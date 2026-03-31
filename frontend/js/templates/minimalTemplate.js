window.ResumeTemplates = window.ResumeTemplates || {};

window.ResumeTemplates.minimal = {
  render: function (data) {
    const edu = (data.education || []).map(item => {
      const when = renderDateRange(item.startDate, item.endDate);
      return `
        <div class="min-item">
          <div class="min-item-main">
            <div class="min-item-title">${escapeHtml(item.degree || '')}</div>
            <div class="min-item-sub">${escapeHtml(item.school || '')}${item.location ? ' • ' + escapeHtml(item.location) : ''}</div>
          </div>
          <div class="min-item-meta">${escapeHtml(when)}</div>
        </div>
      `;
    }).join('');

    const exp = (data.experience || []).map(item => {
      const when = renderDateRange(item.startDate, item.endDate);
      return `
        <div class="min-item">
          <div class="min-item-main">
            <div class="min-item-title">${escapeHtml(item.role || '')}</div>
            <div class="min-item-sub">${escapeHtml(item.company || '')}</div>
          </div>
          <div class="min-item-meta">${escapeHtml(when)}</div>
        </div>
      `;
    }).join('');

    const skills = (data.skills || []).map(s => `<span class="min-pill">${escapeHtml(s.name || '')}</span>`).join('');

    const projects = (data.projects || []).map(item => {
      const link = item.url ? `<a href="${escapeAttr(item.url)}" target="_blank" rel="noopener">${escapeHtml(item.url)}</a>` : '';
      return `
        <div class="min-item min-item-project">
          <div class="min-item-main">
            <div class="min-item-title">${escapeHtml(item.name || '')}</div>
            <div class="min-item-sub">${link}</div>
          </div>
        </div>
      `;
    }).join('');

    return `
      <div class="resume resumeMinimal">
        <header class="min-header">
          <div class="min-name-block">
            <h1 class="min-name">${escapeHtml(data.fullName || '')}</h1>
            ${data.headline ? `<div class="min-headline">${escapeHtml(data.headline)}</div>` : ''}
          </div>
          <div class="min-contact">
            ${data.email ? `<div><i class="fa-regular fa-envelope"></i>${escapeHtml(data.email)}</div>` : ''}
            ${data.phone ? `<div><i class="fa-solid fa-phone"></i>${escapeHtml(data.phone)}</div>` : ''}
            ${data.website ? `<div><i class="fa-solid fa-globe"></i>${escapeHtml(data.website)}</div>` : ''}
            ${data.location ? `<div><i class="fa-solid fa-location-dot"></i>${escapeHtml(data.location)}</div>` : ''}
          </div>
        </header>

        ${data.summary ? `
          <section class="min-section">
            <h2 class="min-section-title">Summary</h2>
            <p class="min-summary">${escapeHtml(data.summary)}</p>
          </section>
        ` : ''}

        <section class="min-section">
          <h2 class="min-section-title">Education</h2>
          ${edu || `<p class="min-empty">Add education entries.</p>`}
        </section>

        <section class="min-section">
          <h2 class="min-section-title">Experience</h2>
          ${exp || `<p class="min-empty">Add experience entries.</p>`}
        </section>

        <section class="min-section">
          <h2 class="min-section-title">Skills</h2>
          ${skills ? `<div class="min-skill-row">${skills}</div>` : `<p class="min-empty">Add skills.</p>`}
        </section>

        <section class="min-section">
          <h2 class="min-section-title">Projects</h2>
          ${projects || `<p class="min-empty">Add projects.</p>`}
        </section>
      </div>
    `;
  }
};


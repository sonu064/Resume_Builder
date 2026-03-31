window.ResumeTemplates = window.ResumeTemplates || {};

window.ResumeTemplates.creative = {
  render: function (data) {
    const edu = (data.education || []).map(item => {
      const when = renderDateRange(item.startDate, item.endDate);
      return `
        <div class="cr-item">
          <div class="cr-item-title">${escapeHtml(item.degree || '')}</div>
          <div class="cr-item-sub">${escapeHtml(item.school || '')}${item.location ? ' • ' + escapeHtml(item.location) : ''}</div>
          <div class="cr-item-meta">${escapeHtml(when)}</div>
        </div>
      `;
    }).join('');

    const exp = (data.experience || []).map(item => {
      const when = renderDateRange(item.startDate, item.endDate);
      return `
        <div class="cr-item">
          <div class="cr-item-title">${escapeHtml(item.role || '')}</div>
          <div class="cr-item-sub">${escapeHtml(item.company || '')}</div>
          <div class="cr-item-meta">${escapeHtml(when)}</div>
        </div>
      `;
    }).join('');

    const skills = (data.skills || []).map(s => `<li><i class="fa-solid fa-circle-small"></i>${escapeHtml(s.name || '')}</li>`).join('');

    const projects = (data.projects || []).map(item => {
      const link = item.url ? `<a href="${escapeAttr(item.url)}" target="_blank" rel="noopener">${escapeHtml(item.url)}</a>` : '';
      return `
        <div class="cr-item">
          <div class="cr-item-title">${escapeHtml(item.name || '')}</div>
          <div class="cr-item-sub">${link}</div>
        </div>
      `;
    }).join('');

    return `
      <div class="resume resumeCreative">
        <aside class="cr-sidebar">
          ${data.profileImageUrl ? `<div class="cr-avatar-wrap"><img class="cr-avatar" src="${escapeAttr(data.profileImageUrl)}" alt="Profile" /></div>` : ''}
          <h1 class="cr-name">${escapeHtml(data.fullName || '')}</h1>
          ${data.headline ? `<div class="cr-headline">${escapeHtml(data.headline)}</div>` : ''}

          <div class="cr-section">
            <h2 class="cr-section-title"><i class="fa-regular fa-id-card"></i>Contact</h2>
            <div class="cr-contact">
              ${data.email ? `<div><i class="fa-regular fa-envelope"></i>${escapeHtml(data.email)}</div>` : ''}
              ${data.phone ? `<div><i class="fa-solid fa-phone"></i>${escapeHtml(data.phone)}</div>` : ''}
              ${data.website ? `<div><i class="fa-solid fa-globe"></i>${escapeHtml(data.website)}</div>` : ''}
              ${data.location ? `<div><i class="fa-solid fa-location-dot"></i>${escapeHtml(data.location)}</div>` : ''}
            </div>
          </div>

          <div class="cr-section">
            <h2 class="cr-section-title"><i class="fa-solid fa-lightbulb"></i>Skills</h2>
            ${skills ? `<ul class="cr-skill-list">${skills}</ul>` : `<div class="cr-empty">Add skills.</div>`}
          </div>
        </aside>

        <main class="cr-main">
          ${data.summary ? `
            <section class="cr-block">
              <h2 class="cr-block-title">Summary</h2>
              <p class="cr-summary">${escapeHtml(data.summary)}</p>
            </section>
          ` : ''}

          <section class="cr-block">
            <h2 class="cr-block-title">Experience</h2>
            ${exp || `<p class="cr-empty">Add experience entries.</p>`}
          </section>

          <section class="cr-block">
            <h2 class="cr-block-title">Education</h2>
            ${edu || `<p class="cr-empty">Add education entries.</p>`}
          </section>

          <section class="cr-block">
            <h2 class="cr-block-title">Projects</h2>
            ${projects || `<p class="cr-empty">Add projects.</p>`}
          </section>
        </main>
      </div>
    `;
  }
};


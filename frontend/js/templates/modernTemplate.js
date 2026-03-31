window.ResumeTemplates = window.ResumeTemplates || {};

window.ResumeTemplates.modern = {
  render: function (data) {
    const edu = (data.education || []).map(item => {
      const when = renderDateRange(item.startDate, item.endDate);
      const desc = item.description ? `<div class="desc">${escapeHtml(item.description)}</div>` : '';
      return `
        <div class="item">
          <div>
            <div class="itemTitle">
              <span>${escapeHtml(item.degree || '')}</span>
              <span>${escapeHtml(when)}</span>
            </div>
            <div class="mutedSmall" style="margin-top:4px;">${escapeHtml(item.school || '')}${item.location ? ' • ' + escapeHtml(item.location) : ''}</div>
            ${desc}
          </div>
        </div>
      `;
    }).join('');

    const exp = (data.experience || []).map(item => {
      const when = renderDateRange(item.startDate, item.endDate);
      const desc = item.description ? `<div class="desc">${escapeHtml(item.description)}</div>` : '';
      return `
        <div class="item">
          <div>
            <div class="itemTitle">
              <span>${escapeHtml(item.role || '')}</span>
              <span>${escapeHtml(when)}</span>
            </div>
            <div class="mutedSmall" style="margin-top:4px;">${escapeHtml(item.company || '')}</div>
            ${desc}
          </div>
        </div>
      `;
    }).join('');

    const skills = (data.skills || []).map(s => `<span class="chip">${escapeHtml(s.name || '')}</span>`).join('');
    const projects = (data.projects || []).map(item => {
      const link = item.url ? `<a href="${escapeAttr(item.url)}" target="_blank" rel="noopener">${escapeHtml(item.url)}</a>` : '';
      const desc = item.description ? `<div class="desc">${escapeHtml(item.description)}</div>` : '';
      const tech = item.techStack ? `<div style="margin-top:6px;">${escapeHtml(item.techStack)}</div>` : '';
      return `
        <div class="item">
          <div>
            <div class="itemTitle">
              <span>${escapeHtml(item.name || '')}</span>
              <span>${link}</span>
            </div>
            ${desc}
            ${tech}
          </div>
        </div>
      `;
    }).join('');

    return `
      <div class="resume resumeModern">
        <div class="header">
          <div>
            ${data.profileImageUrl ? `<img class="profileImg profileImgModern" src="${escapeAttr(data.profileImageUrl)}" alt="Profile" />` : ``}
            <div class="name">${escapeHtml(data.fullName || '')}</div>
            ${data.headline ? `<div class="sub">${escapeHtml(data.headline)}</div>` : ''}
            ${data.summary ? `<div class="sub" style="margin-top:10px;">${escapeHtml(data.summary)}</div>` : ''}
          </div>
          <div class="contact">
            <div>${escapeHtml(data.email || '')}</div>
            <div>${escapeHtml(data.phone || '')}</div>
            <div>${escapeHtml(data.website || '')}</div>
            <div>${escapeHtml(data.location || '')}</div>
          </div>
        </div>

        ${data.education && data.education.length ? `<div class="sectionTitle">Education</div>${edu}` : `<div class="sectionTitle">Education</div><div class="desc">Add education entries.</div>`}
        ${data.experience && data.experience.length ? `<div class="sectionTitle">Experience</div>${exp}` : `<div class="sectionTitle">Experience</div><div class="desc">Add experience entries.</div>`}
        <div class="sectionTitle">Skills</div>
        ${skills ? `<div>${skills}</div>` : `<div class="desc">Add skills.</div>`}
        ${data.projects && data.projects.length ? `<div class="sectionTitle">Projects</div>${projects}` : `<div class="sectionTitle">Projects</div><div class="desc">Add projects.</div>`}
      </div>
    `;
  }
};

function renderDateRange(start, end) {
  const s = start ? String(start) : '';
  const e = end ? String(end) : 'Present';
  if (!s && !end) return '';
  if (s && e) return `${s} - ${e}`;
  return s || e;
}

function escapeHtml(str) {
  return String(str ?? '')
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&#039;');
}

function escapeAttr(str) {
  return escapeHtml(str);
}


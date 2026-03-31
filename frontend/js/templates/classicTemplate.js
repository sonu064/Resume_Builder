window.ResumeTemplates = window.ResumeTemplates || {};

window.ResumeTemplates.classic = {
  render: function (data) {
    const edu = (data.education || []).map(item => {
      const when = renderDateRange(item.startDate, item.endDate);
      const desc = item.description ? `<div class="desc">${escapeHtml(item.description)}</div>` : '';
      return `
        <div class="sectionItem">
          <div class="item">
            <div>
              <strong>${escapeHtml(item.degree || '')}</strong>
              <div style="margin-top:2px;"><span>${escapeHtml(item.school || '')}${item.location ? ' • ' + escapeHtml(item.location) : ''}</span></div>
            </div>
            <div><span>${escapeHtml(when)}</span></div>
          </div>
          ${desc}
        </div>
      `;
    }).join('');

    const exp = (data.experience || []).map(item => {
      const when = renderDateRange(item.startDate, item.endDate);
      const desc = item.description ? `<div class="desc">${escapeHtml(item.description)}</div>` : '';
      return `
        <div class="sectionItem">
          <div class="item">
            <div>
              <strong>${escapeHtml(item.role || '')}</strong>
              <div style="margin-top:2px;"><span>${escapeHtml(item.company || '')}</span></div>
            </div>
            <div><span>${escapeHtml(when)}</span></div>
          </div>
          ${desc}
        </div>
      `;
    }).join('');

    const skills = (data.skills || []).map(s => `<li>${escapeHtml(s.name || '')}</li>`).join('');

    const projects = (data.projects || []).map(item => {
      const link = item.url ? `<a href="${escapeAttr(item.url)}" target="_blank" rel="noopener">${escapeHtml(item.url)}</a>` : '';
      const desc = item.description ? `<div class="desc">${escapeHtml(item.description)}</div>` : '';
      const tech = item.techStack ? `<div style="margin-top:6px;"><span>${escapeHtml(item.techStack)}</span></div>` : '';
      return `
        <div class="sectionItem">
          <div class="item">
            <div>
              <strong>${escapeHtml(item.name || '')}</strong>
              <div style="margin-top:2px;"><span>${link}</span></div>
            </div>
          </div>
          ${desc}
          ${tech}
        </div>
      `;
    }).join('');

    return `
      <div class="resume resumeClassic">
        <div class="header">
          <div>
            ${data.profileImageUrl ? `<img class="profileImg" src="${escapeAttr(data.profileImageUrl)}" alt="Profile" />` : ``}
            <h1 class="name">${escapeHtml(data.fullName || '')}</h1>
            <div class="title">${escapeHtml(data.headline || '')}</div>
          </div>
          <div class="contact">
            <div>${escapeHtml(data.email || '')}</div>
            <div>${escapeHtml(data.phone || '')}</div>
            <div>${escapeHtml(data.website || '')}</div>
            <div>${escapeHtml(data.location || '')}</div>
          </div>
        </div>

        ${data.summary ? `<div class="sectionTitle">Summary</div><div class="desc">${escapeHtml(data.summary)}</div>` : ''}

        <div class="sectionTitle">Education</div>
        ${edu || `<div class="desc">Add education entries.</div>`}

        <div class="sectionTitle">Experience</div>
        ${exp || `<div class="desc">Add experience entries.</div>`}

        <div class="sectionTitle">Skills</div>
        ${skills ? `<ul>${skills}</ul>` : `<div class="desc">Add skills.</div>`}

        <div class="sectionTitle">Projects</div>
        ${projects || `<div class="desc">Add projects.</div>`}
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


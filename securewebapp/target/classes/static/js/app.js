// app.js — tiny helpers for form UX and demo
document.addEventListener('DOMContentLoaded', () => {

  // Enhance file inputs: show selected file name(s)
  document.querySelectorAll('input[type=file]').forEach(input => {
    input.addEventListener('change', (e) => {
      const files = Array.from(e.target.files).map(f => f.name).join(', ');
      let info = e.target.closest('form').querySelector('.file-info');
      if(!info) {
        info = document.createElement('div');
        info.className = 'file-info small-muted mt-2';
        e.target.closest('form').appendChild(info);
      }
      info.textContent = files || 'No file chosen';
    });
  });

  // Simple client-side validation for forms with data-validate
  document.querySelectorAll('form[data-validate="true"]').forEach(f => {
    f.addEventListener('submit', (evt) => {
      let valid = true;
      f.querySelectorAll('[required]').forEach(inp => {
        if(!inp.value.trim()) {
          inp.classList.add('is-invalid');
          valid = false;
        } else {
          inp.classList.remove('is-invalid');
        }
      });
      if(!valid){
        evt.preventDefault();
        evt.stopPropagation();
        // flash message
        if(!document.querySelector('.validation-msg')){
          const msg = document.createElement('div');
          msg.className = 'alert alert-danger validation-msg';
          msg.textContent = 'Please fill required fields.';
          f.prepend(msg);
          setTimeout(()=> msg.remove(), 3000);
        }
      }
    });
  });

});

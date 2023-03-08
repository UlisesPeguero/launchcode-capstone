const THEME_KEY = 'pbs_theme';

function changeTheme(theme) {
  theme ||= 'lightBlue';
  localStorage.setItem(THEME_KEY, theme);
  saveUserTheme(theme);
}

function loadTheme() {
  $('body').removeClass().addClass(localStorage.getItem(THEME_KEY));
}

function saveUserTheme(theme) {
  let request = `/users/saveTheme?theme=${theme}&origin=${document.location.pathname}`;
  document.location.href = request;
}

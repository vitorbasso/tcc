export function validateEmailInput(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

const capitalize = (s: string): string =>
  (s && s[0].toUpperCase() + s.slice(1)) || '';

export default capitalize;

import { createContext, Dispatch, ReactNode, SetStateAction, useState } from 'react';

export type ThemeData = 'light' | 'dark';

type ThemeContextType = {
  theme: ThemeData;
  setTheme: Dispatch<SetStateAction<ThemeData>>;
};

const localStorageTheme = () => {
  return localStorage.getItem('theme') as ThemeData | undefined;
};

const osPreferredTheme = () => {
  return window.matchMedia?.('(prefers-color-scheme: dark)')?.matches
    ? 'dark'
    : 'light';
};

const ThemeContext = createContext<ThemeContextType | undefined>(undefined);

export const ThemeProvider = (props: { children: ReactNode }) => {
  const [theme, setTheme] = useState<ThemeData>(localStorageTheme() ?? osPreferredTheme());

  return (
    <ThemeContext.Provider value={{ theme, setTheme }}>
      {props.children}
    </ThemeContext.Provider>
  );
};

export default ThemeContext;

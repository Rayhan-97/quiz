import { ReactNode } from 'react';
import useTheme from '../hooks/useTheme';

const ThemeWrapper = ({ children }: { children: ReactNode }) => {
  const { theme } = useTheme();

  return (
    <>
      <div id={'theme-wrapper'} className={`theme-${theme}`}>
        {children}
      </div>
    </>
  );
};

export default ThemeWrapper;
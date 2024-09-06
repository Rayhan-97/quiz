import clsx from 'clsx';
import { ReactNode, useState } from 'react';
import { Link, LinkProps } from 'react-router-dom';
import Icons from './Icons';
import useTheme from '../hooks/useTheme';

const spaceEnterHandler = (e: React.KeyboardEvent<Element>, eventHandler: () => void) => {
  if (e.key === 'Enter' || e.key === ' ') {
    eventHandler();
  }
};

const Nav = () => {
  const [isMenuHidden, setIsMenuHidden] = useState(true);

  const handleClickMenuIcon = () => {
    setIsMenuHidden(false);
  };

  const handleClickCloseIcon = () => {
    setIsMenuHidden(true);
  };

  return (
    <>
      <div className="nav-container">
        <nav>
          <Link to="/"><Icons.Logo /></Link>
          <Link to="/"><Icons.FullLogo /></Link>
          <div>
            <DarkModeToggle />
            <LinkButton to={'/login'} tabIndex={-1} type={'primary'}>Sign in</LinkButton>
            <LinkButton to={'/register'} tabIndex={-1} type={'secondary'}>Sign up</LinkButton>
            <Icons.HamburgerMenu
              className={clsx(!isMenuHidden && 'hidden')}
              focusable={true}
              tabIndex={0}
              onClick={handleClickMenuIcon}
              onKeyDown={(e) => spaceEnterHandler(e, handleClickMenuIcon)}
            />
            <Icons.Close
              className={clsx(isMenuHidden && 'hidden')}
              focusable={true}
              tabIndex={0}
              onClick={handleClickCloseIcon}
              onKeyDown={(e) => spaceEnterHandler(e, handleClickCloseIcon)}
            />
          </div>
        </nav>

        <menu className={clsx(isMenuHidden && 'hidden')}>
          <LinkButton to={'/login'} tabIndex={-1} type={'primary'}>Sign in</LinkButton>
          <LinkButton to={'/register'} tabIndex={-1} type={'secondary'}>Sign up</LinkButton>
        </menu>
      </div>
    </>
  );
};

const DarkModeToggle = () => {
  const { theme, setTheme } = useTheme();
  const [isDarkMode, setIsDarkMode] = useState(theme === 'dark');

  const handleSunClick = () => {
    setTheme('light');
    setIsDarkMode(false);
  };

  const handleMoonClick = () => {
    setTheme('dark');
    setIsDarkMode(true);
  };

  return (
    <>
      <Icons.Sun
        className={clsx(!isDarkMode && 'hidden')}
        focusable={true}
        tabIndex={0}
        onClick={handleSunClick}
        onKeyDown={(e) => spaceEnterHandler(e, handleSunClick)}
      />
      <Icons.Moon
        className={clsx(isDarkMode && 'hidden')}
        focusable={true}
        tabIndex={0}
        onClick={handleMoonClick}
        onKeyDown={(e) => spaceEnterHandler(e, handleMoonClick)}
      />
    </>
  );
};

type ButtonType = 'primary' | 'secondary';

type LinkButtonProps = LinkProps
  & React.RefAttributes<HTMLAnchorElement>
  & {
    children: ReactNode,
    type: ButtonType
  }

const LinkButton = ({ children, type, ...props }: LinkButtonProps) => {
  return (
    <>
      <Link {...props}>
        <button className={`link-button ${type}`}>
          {children}
        </button>
      </Link>
    </>
  );
};

export default Nav;
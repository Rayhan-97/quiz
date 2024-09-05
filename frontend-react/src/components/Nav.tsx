import clsx from 'clsx';
import { ReactNode, useState } from 'react';
import { Link, LinkProps } from 'react-router-dom';
import Icons from './Icons';

const Nav = () => {
  const [isMenuHidden, setIsMenuHidden] = useState(true);

  const handleClickMenuIcon = () => {
    setIsMenuHidden(false);
  };

  const handleClickCloseIcon = () => {
    setIsMenuHidden(true);
  };

  const spaceEnterHandler = (e: React.KeyboardEvent<Element>, eventHandler: () => void) => {
    if (e.key === 'Enter' || e.key === ' ') {
      eventHandler();
    }
  };

  return (
    <>
      <div className="nav-container">
        <nav>
          <Link to="/"><Icons.Logo /></Link>
          <Link to="/"><Icons.FullLogo /></Link>
          <div>
            <Icons.Sun focusable={true} tabIndex={0} />
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
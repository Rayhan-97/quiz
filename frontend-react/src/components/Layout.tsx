import PropTypes from 'prop-types';
import { ReactNode } from 'react';
import { Outlet } from 'react-router-dom';
import '../assets/css/styles.css';
import Nav from './Nav';

const Layout = () => {
  return (
    <>
      <Nav />
      <ContentContainer>
        <Outlet />
      </ContentContainer>
    </>
  );
};

interface ContentContainerProps {
  children: ReactNode;
}

const ContentContainer: React.FC<ContentContainerProps> = ({ children }) => {
  return (
    <div className={'content-container'}>{children}</div>
  );
};

ContentContainer.propTypes = {
  children: PropTypes.node.isRequired,
};
export default Layout;

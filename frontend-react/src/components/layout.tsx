import { Outlet, Link } from "react-router-dom";
import PropTypes from 'prop-types';
import { ReactNode } from "react";
import '../assets/css/styles.css';
import useAuth from "../hooks/useAuth";

const Layout = () => {
  return (
    <>
      <nav>
        <ul>
          <li>
            <Link to="/">Home</Link>
          </li>
          <li>
            <Link to="/register">Register</Link>
          </li>
          <li>
            <Link to="/login">Login</Link>
          </li>
          <li>
            <Link to="/make">Make</Link>
          </li>
        </ul>
      </nav>

      <ContentContainer>
        <Outlet />
      </ContentContainer>
    </>
  )
};

interface ContentContainerProps {
  children: ReactNode;
}

const ContentContainer: React.FC<ContentContainerProps> = ({ children }) => {
  return (
    <div className="content-container">{children}</div>
  );
};

ContentContainer.propTypes = {
  children: PropTypes.node.isRequired,
};
export default Layout;
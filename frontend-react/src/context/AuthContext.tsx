import { createContext, Dispatch, ReactNode, SetStateAction, useState } from 'react';

type AuthDataType = { accessToken: string };

type AuthContextType = {
  auth: AuthDataType;
  setAuth: Dispatch<SetStateAction<AuthDataType>>;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = (props: { children: ReactNode }) => {
  const [auth, setAuth] = useState<AuthDataType>({ accessToken: '' });

  return (
    <AuthContext.Provider value={{ auth, setAuth }}>
      {props.children}
    </AuthContext.Provider>
  );
};

export default AuthContext;

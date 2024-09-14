import { ReactNode } from 'react';
import { ReactComponent as HamburgerIcon } from '../assets/icons/hamburgerMenu.svg';
import { ReactComponent as CloseIcon } from '../assets/icons/close.svg';
import { ReactComponent as SunIcon } from '../assets/icons/sun.svg';
import { ReactComponent as MoonIcon } from '../assets/icons/moon.svg';
import { ReactComponent as ErrorIcon } from '../assets/icons/error.svg';
import { ReactComponent as OpenEyeIcon } from '../assets/icons/openEye.svg';
import { ReactComponent as ClosedEyeIcon } from '../assets/icons/closedEye.svg';
import { ReactComponent as QuoteMarksSvg } from '../assets/icons/quoteMarks.svg';
import { ReactComponent as FullLogoSvg } from '../assets/icons/fullLogo.svg';
import { ReactComponent as LogoSvg } from '../assets/icons/logo.svg';
import clsx from 'clsx';

// this is height/width they will be set to 100% internally here
// so the height/width can be controlled with CSS
type SvgIconProps = Omit<React.SVGProps<SVGSVGElement>, 'height' | 'width'>;

type BaseIconProps = {
    children: ReactNode,
    name: string,
    clickable?: boolean
}

const BaseIcon = ({ children, name, clickable = false }: BaseIconProps) => {
    return <>
        <div className={'icon-wrapper'}>
            <div className={clsx('icon', name, clickable && 'clickable')}>
                {children}
            </div>
        </div>
    </>;
};

const HamburgerMenu = (props: SvgIconProps) => {
    return <>
        <BaseIcon name={'hamburger-menu'} clickable={true}>
            <HamburgerIcon height={'100%'} width={'100%'} {...props} />
        </BaseIcon>
    </>;
};

const Close = (props: SvgIconProps) => {
    return <>
        <BaseIcon name={'close'} clickable={true}>
            <CloseIcon height={'100%'} width={'100%'} {...props} />
        </BaseIcon>
    </>;
};

const Sun = (props: SvgIconProps) => {
    return <>
        <BaseIcon name={'sun'} clickable={true}>
            <SunIcon height={'100%'} width={'100%'} {...props} />
        </BaseIcon>
    </>;
};

const Moon = (props: SvgIconProps) => {
    return <>
        <BaseIcon name={'moon'} clickable={true}>
            <MoonIcon height={'100%'} width={'100%'} {...props} />
        </BaseIcon>
    </>;
};

const Error = (props: SvgIconProps) => {
    return <>
        <BaseIcon name={'error'}>
            <ErrorIcon height={'100%'} width={'100%'} {...props} />
        </BaseIcon>
    </>;
};

const OpenEye = (props: SvgIconProps) => {
    return <>
        <BaseIcon name={'open-eye'} clickable={true}>
            <OpenEyeIcon height={'100%'} width={'100%'} {...props} />
        </BaseIcon>
    </>;
};

const ClosedEye = (props: SvgIconProps) => {
    return <>
        <BaseIcon name={'closed-eye'} clickable={true}>
            <ClosedEyeIcon height={'100%'} width={'100%'} {...props} />
        </BaseIcon>
    </>;
};

const QuoteMarks = (props: SvgIconProps) => {
    return <>
        <BaseIcon name={'quote-marks'}>
            <QuoteMarksSvg height={'100%'} width={'100%'} {...props} />
        </BaseIcon>
    </>;
};


const FullLogo = (props: SvgIconProps) => {
    return <>
        <BaseIcon name={'full-logo'}>
            <FullLogoSvg height={'100%'} width={'100%'} {...props} />
        </BaseIcon>
    </>;
};

const Logo = (props: SvgIconProps) => {
    return <>
        <BaseIcon name={'logo'}>
            <LogoSvg height={'100%'} width={'100%'} {...props} />
        </BaseIcon>
    </>;
};



const Icons = {
    HamburgerMenu,
    Close,
    Sun,
    Moon,

    OpenEye,
    ClosedEye,
    
    Error,

    QuoteMarks,

    FullLogo,
    Logo,
};

export default Icons;

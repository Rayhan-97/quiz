import { ReactNode } from 'react';
import { ReactComponent as CreativeThinkingSvg } from '../assets/icons/creativeThinking.svg';
import { ReactComponent as SharingSvg } from '../assets/icons/sharing.svg';
import { ReactComponent as AnalyseSvg } from '../assets/icons/analyse.svg';
import clsx from 'clsx';

// this is height/width they will be set to 100% internally here
// so the height/width can be controlled with CSS
type SvgImageProps = Omit<React.SVGProps<SVGSVGElement>, 'height' | 'width'>;

type BaseImageProps = {
    children: ReactNode,
    name: string,
}

const Image = ({ children, name }: BaseImageProps) => {
    return <>
        <div className={clsx('image', name)}>
            {children}
        </div>
    </>;
};

const CreativeThinking = (props: SvgImageProps) => {
    return <>
        <Image name={'creative-thinking'}>
            <CreativeThinkingSvg height={'100%'} width={'100%'} {...props} />
        </Image>
    </>;
};

const Sharing = (props: SvgImageProps) => {
    return <>
        <Image name={'sharing'}>
            <SharingSvg height={'100%'} width={'100%'} {...props} />
        </Image>
    </>;
};

const Analysing = (props: SvgImageProps) => {
    return <>
        <Image name={'analysing'}>
            <AnalyseSvg height={'100%'} width={'100%'} {...props} />
        </Image>
    </>;
};


const Images = {
    CreativeThinking,
    Sharing,
    Analysing
};

export default Images;
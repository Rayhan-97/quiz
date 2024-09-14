import { useState } from 'react';
import { InView } from 'react-intersection-observer';
import puzzleImage from '../assets/images/alicia-christin-gerald-55nRTtBgP74-unsplash.jpg';
import catImage from '../assets/images/close-up-adorable-kitten-indoors.jpg';
import annaImage from '../assets/images/young-woman-long-hair-character.png';
import quotationMarksImage from '../assets/images/quotationMarks.png';
import Icons from './Icons';
import Images from './Images';
import { LinkButton } from './Nav';

const Home = () => {

    return (<>
        <div id={'hero-section'} className={'section-container'}>
            <section>
                <article>
                    <header className={'heading-body-container'}>
                        <h1>Bringing people <br />together</h1>
                        <p>Show off what you know and learn what you don&rsquo;t. Make your own quiz today!</p>
                    </header>
                    <LinkButton to={'/login'} tabIndex={-1} type={'primary'}>Get started</LinkButton>
                </article>
                <img src={puzzleImage} alt={'Puzzle'} />
            </section>
        </div>

        <div id={'quick-start-guide'} className={'section-container centered-section'}>
            <section>
                <header className={'heading-body-container'}>
                    <h1>Quick start guide</h1>
                    <p>OurQuiz helps you create, share and monitor your quizzes in real time</p>
                </header>
                <div className={'cards-container'}>
                    <div className={'card'}>
                        <h2>Create</h2>
                        <Images.CreativeThinking />
                        <p>Create quiz from scratch or with templates. Set questions, answers, time limits and points.</p>
                    </div>
                    <div className={'card'}>
                        <h2>Share</h2>
                        <Images.Sharing />
                        <p>Share a join code with friends, family, co-workers, students, everyone, to join in on the fun. Start the quiz!</p>
                    </div>
                    <div className={'card'}>
                        <h2>Monitor</h2>
                        <Images.Analysing />
                        <p>Check leader boards in real time. See past quiz history, quizzes you&rsquo;ve created and quizzes taken</p>
                    </div>
                </div>
            </section>
        </div>

        <TestimonialSection />

        <div id={'image-credits'} className={'section-container centered-section'}>
            <section>
                <header className={'heading-body-container'}>
                    <h1>Image credits</h1>
                    <p>Attributing the creators of the images used to bring a spark to OurQuiz</p>
                </header>
                <article className={'image-credits-container'}>
                    <a
                        target={'_blank'}
                        href={'https://unsplash.com/photos/a-close-up-of-a-book-55nRTtBgP74?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash'}
                        className={'image-credits-container'}
                        rel={'noreferrer'}
                    >
                        <div className={'image'}>
                            <img src={puzzleImage} alt={'Puzzle'} />
                        </div>
                        <p className={'credits'}>Image by Alicia Christin Gerald on Unsplash</p>
                    </a>
                    <a
                        target={'_blank'}
                        href={'https://www.freepik.com/free-vector/organic-flat-creativity-illustration-with-people_13914816.htm#fromView=search&page=2&position=16&uuid=aad2c48f-a4b6-4276-ae80-eacc0165ba82'}
                        className={'image-credits-container'}
                        rel={'noreferrer'}
                    >
                        <div className={'image'}>
                            <Images.CreativeThinking />
                        </div>
                        <p className={'credits'}>Image by freepik on Freepik</p>
                    </a>
                    <a
                        target={'_blank'}
                        href={'https://www.freepik.com/free-vector/character-illustration-people-holding-speech-bubbles_3425174.htm#fromView=search&page=2&position=14&uuid=996e99a3-22c5-4a13-a26a-5a26e372d3d9'}
                        className={'image-credits-container'}
                        rel={'noreferrer'}
                    >
                        <div className={'image'}>
                            <Images.Sharing />
                        </div>
                        <p className={'credits'}>Image by rawpixel.com on Freepik</p>
                    </a>
                    <a
                        target={'_blank'}
                        href={'https://www.freepik.com/free-vector/business-audit-financial-specialist-cartoon-character-with-magnifier-examination-statistical-graphic-information-statistics-diagram-chart-vector-isolated-concept-metaphor-illustration_12083507.htm#fromView=search&page=1&position=10&uuid=924a0474-f006-4a01-bf36-94a735a5b982'}
                        className={'image-credits-container'}
                        rel={'noreferrer'}
                    >
                        <div className={'image'}>
                            <Images.Analysing />
                        </div>
                        <p className={'credits'}>Image by vectorjuice on Freepik</p>
                    </a>
                    <a
                        target={'_blank'}
                        href={'https://www.freepik.com/free-ai-image/close-up-adorable-kitten-indoors_65609606.htm#fromView=search&page=1&position=6&uuid=614b7f45-2648-461a-b50c-0370a63f28ee'}
                        className={'image-credits-container'}
                        rel={'noreferrer'}
                    >
                        <div className={'image'}>
                            <img src={catImage} alt={'Cat'} />
                        </div>
                        <p className={'credits'}>Image by freepik AI on Freepik</p>
                    </a>
                    <a
                        target={'_blank'}
                        href={'https://www.freepik.com/free-vector/young-woman-long-hair-character_169501848.htm#fromView=search&page=1&position=41&uuid=f4ac38f3-f978-43b3-b49d-378cedc4c9fa'}
                        className={'image-credits-container'}
                        rel={'noreferrer'}
                    >
                        <div className={'image'}>
                            <img src={annaImage} alt={'Girl with long hair'} />
                        </div>
                        <p className={'credits'}>Image by studiogstock on Freepik</p>
                    </a>
                    <a
                        target={'_blank'}
                        href={'https://www.freepik.com/free-vector/abstract-quotation-marks-set_44471916.htm#fromView=search&page=4&position=26&uuid=2d165d50-29b6-4f28-9024-43f028b36952'}
                        className={'image-credits-container'}
                        rel={'noreferrer'}
                    >
                        <div className={'image'}>
                            <img src={quotationMarksImage} alt={'Quotation marks'} />
                        </div>
                        <p className={'credits'}>Image by juicy_fish on Freepik</p>
                    </a>
                </article>
            </section>
        </div>
    </>
    );
};

const slides = ['slide-1', 'slide-2', 'slide-3'];

const TestimonialSection = () => {
    const [activeSlide, setActiveSlide] = useState<number>(1);

    const handleScroll = (inView: boolean, entry: IntersectionObserverEntry) => {
        if (inView) {
            setActiveSlide(+entry.target.id.slice(-1));
        }
    };

    const handleClick = (slide: number) => {
        const testimonialElement = document.getElementById(`slide-${slide}`);
        testimonialElement?.scrollIntoView({ block: 'nearest' });
    };

    return (<>
        <div id={'testimonials'} className={'section-container'}>
            <section>
                <header className={'heading-body-container'}>
                    <h2>Hear from OurQuiz users</h2>
                    <p>&ldquo;Real&rdquo; stories from people who have used OurQuiz with friends, families, co-workers and students</p>
                </header>
                <article>
                    <div className={'slider-container'}>
                        <div className={'slider'}>
                            <InView onChange={handleScroll} threshold={0.55}>
                                {({ ref }) => (
                                    <div className={'testimonial-wrapper'}>
                                        <div ref={ref} id={slides[0]} className={'testimonial'}>
                                            <div className={'profile cat-profile'}></div>
                                            <div className={'author-details'}>Meowski. Textiles teacher at Catworts</div>
                                            <p>OurQuiz has been an excellent tool for learning in my textiles classes. The students are really engaged and it&rsquo;s a fun way to keep them motivated. Perhaps one day, they might even surpass my yarn handling abilities!</p>
                                            <Icons.QuoteMarks />
                                            <Icons.QuoteMarks />
                                        </div>
                                    </div>
                                )}
                            </InView>
                            <InView onChange={handleScroll} threshold={0.55}>
                                {({ ref }) => (
                                    <div className={'testimonial-wrapper'}>
                                        <div ref={ref} id={slides[1]} className={'testimonial'}>
                                            <div className={'profile anna-profile'}></div>
                                            <div className={'author-details'}>Anna. Mother of 4</div>
                                            <p>Family game night is incomplete without OurQuiz! It&rsquo;s great fun putting our knowledge to the test. The kids know Harry Potter a little too well and know their country flags too now. Makes me proud!</p>
                                            <Icons.QuoteMarks />
                                            <Icons.QuoteMarks />
                                        </div>
                                    </div>
                                )}
                            </InView>
                            <InView onChange={handleScroll} threshold={0.55}>
                                {({ ref }) => (
                                    <div className={'testimonial-wrapper'}>
                                        <div ref={ref} id={slides[2]} className={'testimonial'}>
                                            <div className={'profile creator-profile'}></div>
                                            <div className={'author-details'}>Rayhan. OurQuiz creator</div>
                                            <p>I&rsquo;ve never experienced such an incredible work of art. OurQuiz: so smooth, well designed and implemented. Whoever made it must be really awesome and employable.</p>
                                            <Icons.QuoteMarks />
                                            <Icons.QuoteMarks />
                                        </div>
                                    </div>
                                )}
                            </InView>
                        </div>
                        <aside className={'testimonial-nav'}>
                            {slides.map((slide, index) => (
                                index === activeSlide - 1
                                    ? <button className={'active'} key={index}></button>
                                    : <button onClick={() => handleClick(index + 1)} key={index}></button>
                            ))}
                        </aside>
                    </div>
                </article>
            </section>
        </div>
    </>
    );
};

export default Home;

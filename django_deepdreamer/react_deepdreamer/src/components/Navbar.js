import React, {useState} from 'react';
import {Link} from 'react-router-dom';
import './Navbar.css';

function Navbar() {
    const [click, setClick] = useState(false);
    
    const handleClick = () => setClick(!click);
    const closeMobileMenu = () => setClick(false);
    
    return (
        <>
            <div>
                <nav className="navbar">
                    <div className="navbar-container">
                        <Link to="/" className="navbar-logo" onClick={closeMobileMenu}>
                            DeepDreamer<i class="fa-solid fa-frog"/>
                        </Link>
                    </div>
                    <div className="icon-container">
                        <div className='menu-icon' onClick={handleClick}>
                            <i className={click ? 'fas fa-times' : 'fas fa-bars'} />
                        </div>
                    </div>
                    <ul className={click ? 'nav-menu active' : 'nav-menu'}>
                            <li className='nav-item'>
                                <Link to='/' className='nav-links' onclick={closeMobileMenu}>
                                    Home
                                </Link>
                            </li>
                            <li className='nav-item'>
                                <Link to='/Dreamify' className='nav-links' onclick={closeMobileMenu}>
                                    Dreamify
                                </Link>
                            </li>
                            <li className='nav-item'>
                                <Link to='/Gallery' className='nav-links' onclick={closeMobileMenu}>
                                    Gallery
                                </Link>
                            </li>
                            <li className='nav-item'>
                                <Link to='/About' className='nav-links' onclick={closeMobileMenu}>
                                    About
                                </Link>
                            </li>
                            <li className='nav-item'>
                                <a href='https://github.com/huberlw/DeepDream-Image-Maker/tree/gui' target="_blank" rel="noopener noreferrer" className='nav-links'>
                                    Download
                                </a>
                            </li>
                    </ul>
                </nav>
            </div>
        </>
  );
}

export default Navbar;
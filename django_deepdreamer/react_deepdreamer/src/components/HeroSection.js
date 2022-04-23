import React from 'react'
import '../App.css'
import './HeroSection.css'
import { Button } from './Button'

function HeroSection() {
  return (
    <>
        <div className='hero-container'>
            <h1>DREAMIFY</h1>
            <p>A single dream is more powerful than a thousand realities - J.R.R. Tolkien</p>
            <div className="hero-btns">
                <Button className='btns' buttonStyle='btn--primary' buttonSize='btn--large'>
                    DREAMIFY
                </Button>
            </div>
        </div>
    </>
  )
}

export default HeroSection
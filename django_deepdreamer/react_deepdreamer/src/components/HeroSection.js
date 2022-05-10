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
            <p>DeepDreamer uses Google's DeepDream technology to transform</p> 
            <p>your photos into something new and exciting!</p>
            <p>Click the "Dreamify" tab to get started!</p>
        </div>
    </>
  )
}

export default HeroSection
import React from 'react'
import '../../App.css'
import Cards from '../Cards';
import './Gallery.css'

export default function Gallery() {
  return <div className='gallery-container'>
            <h1 className='header'>GALLERY  <i class="fa-solid fa-image"></i></h1>
            <br/><br/>
            <Cards />
            <br/><br/><br/><br/><br/>
        </div>
}
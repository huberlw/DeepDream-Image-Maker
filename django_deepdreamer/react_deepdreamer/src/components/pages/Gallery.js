import React from 'react'
import '../../App.css'
import './Gallery.css'
import Art from './Art'

export default function Gallery() {
  return (<div className='gallery-container'>
            <div className='inside-gallery'>
              {/*<h1 className='header'>GALLERY  <i class="fa-solid fa-image"></i></h1>/*/}
              <Art/>
            </div>
         </div>);
}
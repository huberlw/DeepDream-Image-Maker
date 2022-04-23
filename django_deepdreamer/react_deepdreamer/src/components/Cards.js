import React from 'react'
import CardItem from './CardItem'
import './Cards.css'

function Cards() {
  return (
    <div className='cards'>
        <h1>Drift away</h1>
        <div className="cards__contain">
            <div className="cards__wrapper">
                <ul className="cards__items">
                    <CardItem src="../../../../images/standard/einstein.png"
                        text="A truly relativistic Einstein"
                        label="Image"
                        path='/Gallery'/>

                    <CardItem src="../../../../images/standard/google.png"
                        text="A product of Google (not really)"
                        label="Image"
                        path='/Gallery'/>

                    <CardItem src="../../../../images/standard/penguin.png"
                        text="A new Antarctica"
                        label="Image"
                        path='/Gallery'/>

                    <CardItem src="../../../../images/standard/dog.png"
                        text="A brand new breed"
                        label="Image"
                        path='/Gallery'/>
                </ul>
            </div>
        </div>
    </div>
  )
}

export default Cards
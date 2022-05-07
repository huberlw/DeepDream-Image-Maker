import './App.css';
import React, { Component } from 'react'
import Navbar from './components/Navbar';
import Footer from './components/Footer'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import Home from './components/pages/Home';
import Dreamify from './components/pages/Dreamify';
import Gallery from './components/pages/Gallery';
import About from './components/pages/About';

export class App extends Component {  
  render() {
    return (
      <>
      <Router>       
          <Routes>
            <Route exact path='/' element={<Home/>} />
            <Route exact path='/Dreamify' element={<Dreamify/>} />
            <Route exact path='/Gallery' element={<Gallery/>} />
            <Route exact path='/About' element={<About/>} />
          </Routes>
          <Navbar />
          <Footer />
        </Router>
      </>
    )
  }
}

export default App;

import './App.css';
import React, { Component } from 'react'

export class App extends Component {
  state={tmpImg: 'https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_640.png'}

  imgHandler = (e) => {
    const reader = new FileReader();
    reader.onload = () => {
      if(reader.readyState === 2){
        this.setState({tmpImg: reader.result})
      }
    }
    reader.readAsDataURL(e.target.files[0])
  }

  render() {
    const {tmpImg} = this.state
    return (
      <div className="page">
        <div clasName="container">
          <h1 className="heading">Dreamify</h1>
          <div>
            <img src={tmpImg} alt="" id="img"></img>
          </div>
          <input type="file" className="open" name="img-up" id="input" accept="image\*"
          onChange={this.imgHandler}/>
        </div>
        <form action="/sweet_dreams">
          <button>Dreamify</button>
        </form>
      </div>
    )
  }
}

export default App;

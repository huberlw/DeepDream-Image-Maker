import './App.css';
import React, { Component } from 'react'

export class App extends Component {  
  constructor(props) {
    super(props);
    this.state = {
      name: "",
      img: [],
      tmpImg: 'https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_640.png'
    }
  }

  imgHandler = (e) => {
    this.setState({
      img: e.target.files[0]
    })
    
    const reader = new FileReader();
    reader.onload = () => {
      if(reader.readyState === 2){
        this.setState({tmpImg: reader.result})
      }
    }
    reader.readAsDataURL(e.target.files[0])
  }

  newDream = () => {
    const uploadData = new FormData();
    uploadData.append('name', this.state.name)
    uploadData.append('img', this.state.img, this.state.img.name)
    
    fetch('http://127.0.0.1:8000/sleepy/', {
      method: 'POST',
      body: uploadData
    })
    .then(res => console.log(res))
    .catch(error => console.log(error))
  }

  render() {
    const {tmpImg} = this.state
    return (
      <div className="page">
        <div className="container">
          <h1 className="heading">Dreamify</h1>
          <div className="img-holder">
            <img src={tmpImg} alt="" id="img" className="img"/>
          </div>
          <label className="text">File Name:
            <input type="text" onChange={(e) => this.setState({name: e.target.value})}/>
          </label>
          <br/>
          <label className="text">Image
            <input type="file" name="img-up" id="input" accept="image\*" onChange={this.imgHandler}/>
          </label>
          <br/>
          <button className="btn" onClick={this.newDream}>Dreamify</button>
        </div>
      </div>
    )
  }
}

export default App;

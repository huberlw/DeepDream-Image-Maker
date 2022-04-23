import './App.css';
import React, { Component } from 'react'

export class App extends Component {  
  constructor(props) {
    super(props);
    this.state = {
      img: [],
      done: false,
      tmpImg: 'https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_640.png',
    }
  }

  imgHandler = (e) => {
    this.setState({img: e.target.files[0]})
    
    const reader = new FileReader();
    reader.onload = () => {
      if(reader.readyState === 2){
        this.setState({tmpImg: reader.result})
      }
    }
    reader.readAsDataURL(e.target.files[0])
  }

  urlHandler = (e) => {
    this.setState({tmpImg: e.target.value})
  }

  newDream = () => {
    const uploadData = new FormData();
    uploadData.append('img', this.state.img, this.state.img.name)
    uploadData.append('done', this.state.done)
    
    fetch('http://127.0.0.1:8000/sleepy/', {
      method: 'POST',
      body: uploadData
    })
    .then(this.setState({tmpImg: 'http://127.0.0.1:8000/images/loading.gif'}))
    .then(res => console.log(res))
    .then(this.displayImage()) // ABSOLUTE TRASH: REMOVE LATER
    .catch(error => console.log(error))
  }

  // ABSOLUTE TRASH: REMOVE LATER
  displayImage()
  {
    setTimeout(
      function() {
        this.setState({tmpImg: 'http://127.0.0.1:8000/images/dream.png'});
      }
      .bind(this),
      45000
    );
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
          <label className="text">Image File
            <input type="file" name="img-up" id="input" accept="image\*" onChange={this.imgHandler}/>
          </label>
          <br/>
          <label className="text">Image URL
            <input type="text" placeholder="Enter URL" onChange={this.urlHandler}/>
          </label>
          <button className="btn" onClick={this.newDream}>Dreamify</button>
        </div>
      </div>
    )
  }
}

export default App;

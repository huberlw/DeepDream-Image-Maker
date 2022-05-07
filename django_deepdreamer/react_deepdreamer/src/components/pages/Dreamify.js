import './Dreamify.css'
import React, { Component, useState} from 'react'
import DreamifyBtn from '../dreamify_buttons/DreamifyBtn';
import DownloadBtn from '../dreamify_buttons/DownloadBtn';
import UploadBtn from '../dreamify_buttons/UploadBtn'
import ResetBtn from '../dreamify_buttons/ResetBtn';
import fileDownload from 'js-file-download'
import axios from 'axios'

export class Dreamify extends Component {  
    constructor(props) {
      super(props);
      this.state = {
        img: [],
        done: false,
        tmpImg: '../../../../images/standard/blank.jpg',
        path: ''
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
  
    newDream = () => {
      const uploadData = new FormData();
      uploadData.append('img', this.state.img, this.state.img.name)
      uploadData.append('done', this.state.done)
      
      fetch('/sleepy/', {
        method: 'POST',
        body: uploadData
      })
      .then(this.setState({tmpImg: '../../../../images/standard/loading.gif'}))
      .then(res => this.resHelper(res))
      .catch(error => console.log(error))
    }

    resHelper = (res) => {
      this.setState({tmpImg: `../../../../images/${this.state.img.name}`})
      this.setState({done: true})
      console.log(res)
    }

    download = () => {
      axios.get(this.state.tmpImg,  {
        responseType: 'blob'
      })
      .then(res => {
        fileDownload(res.data, this.state.img.name)
      })
    }

    reset = () => {
      this.setState({done: false})
    }
  
    render() {
      const {tmpImg} = this.state

      return (
        <div className="dream-page">
            <div className="dream-container">
                <h1 className="dream-heading">Dreamify  <i class="fa-solid fa-bed" /></h1>
                <div className="dream-img-holder">
                    <img src={tmpImg} alt="" id="img" className="dream-img"/>
                </div>
                <table className ="btnTable">
                  <tr>
                    <td>                                    
                          {this.state.done ? < ResetBtn onClick={this.reset}/> : <UploadBtn onChange={this.imgHandler} />}                         
                    </td>
                      <td>
                          {this.state.done ? <DownloadBtn onClick={this.download} /> : <DreamifyBtn onClick={this.newDream} />}
                     </td>
                  </tr>
                </table>
            </div>
        </div>
      )
    }
  }

  export default Dreamify
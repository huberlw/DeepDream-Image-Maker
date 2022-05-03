import './UploadBtn.css'
import React from 'react'

function UploadBtn(props) {
    return (
        <div>
            <input type="file" name="img-up" id="input" accept="image\*" onChange={props.onChange}/>
                <label htmlFor="input" className="dream-image-upload">
                    <i class="fa-regular fa-image"></i>  Choose Image
                </label>
        </div>
    )
}

export default UploadBtn
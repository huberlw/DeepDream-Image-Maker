import './Buttons.css'
import React from 'react'

function UploadBtn(props) {
    return (
        <div>
            <input type="file" name="img-up" id="input" accept="image\*" onChange={props.onChange}/>
<<<<<<< Updated upstream
                <label htmlFor="input" className="DreamifyButtons">
                    <i class="fa-regular fa-image"></i>  Choose Image
=======
                <label htmlFor="input" className="dream-image-upload">
                    Choose Image
>>>>>>> Stashed changes
                </label>
        </div>
    )
}

export default UploadBtn
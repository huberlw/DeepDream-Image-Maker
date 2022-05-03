import './UploadBtn.css'
import React from 'react'

function ResetBtn(props) {
    return (
        <button className="reset-btn" onClick={props.onClick}>Reset</button>
    )
}

export default ResetBtn
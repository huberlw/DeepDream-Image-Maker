import './DownloadBtn.css'
import React from 'react'

function DownloadBtn(props) {
    return (
        <button className="download-btn" onClick={props.onClick}>Download Image</button>
    )
}

export default DownloadBtn

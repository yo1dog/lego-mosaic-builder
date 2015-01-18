const MAX_MOSAIC_STUD_WIDTH  = 200;
const MAX_MOSAIC_STUD_HEIGHT = 200;

const DEFAULT_MOSAIC_STUD_WIDTH  = 50;
const DEFAULT_MOSAIC_STUD_HEIGHT = 50;


function LegoMosaicBuilder(_canvas) {
  this.canvas = _canvas;
  this.canvas.ctx = this.canvas.getContext("2d");
  
  this.originalImageCanvas = null;
  
  this.mosaicImageData = null;
  
  this.mosaicMaxStudWidth  = DEFAULT_MOSAIC_STUD_WIDTH;
  this.mosaicMaxStudHeight = DEFAULT_MOSAIC_STUD_HEIGHT;
  this.mosaicStudWidth  = 0;
  this.mosaicStudHeight = 0;
}

/**
 * Load Image
 * 
 * Loads a new image onto the canvas and into memory.
 * 
 * Callback parameters:
 *   err - Error object on error or NULL.
 */
LegoMosaicBuilder.prototype.loadImage = function(file, rootCB/*(err)*/) {
  var image = new Image();
  var returned = false;
  
  image.onload = (function() {
    return imageCB.bind(this)();
  }).bind(this);
  
  image.onerror = (function() {
    return imageCB.bind(this, new Error("Invalid image."))();
  }).bind(this);
  
  image.src = URL.createObjectURL(file);
  
  
  function imageCB(err) {
    if (returned) return;
    returned = true;
    
    URL.revokeObjectURL(image.src);
    
    if (err) return rootCB(err);
    
    // scale image to max allowed width and height
    var dimensions = LegoMosaicBuilder.getFillDimensions(image.width, image.height, MAX_MOSAIC_STUD_WIDTH, MAX_MOSAIC_STUD_HEIGHT);
    this.originalImageCanvas = LegoMosaicBuilder.scaleImage(image, dimensions.width, dimensions.height);
    
    this.refreshMosaicSize();
    this.refreshMosaicImageData();
    this.redrawMosaic();
    
    return rootCB();
  }
};


LegoMosaicBuilder.prototype.resizeMosaic = function(studWidth, studHeight) {
  this.mosaicMaxStudWidth  = studWidth;
  this.mosaicMaxStudHeight = studHeight;
  
  this.refreshMosaicSize();
  this.refreshMosaicImageData();
  this.redrawMosaic();
};


LegoMosaicBuilder.prototype.refreshMosaicSize = function() {
  // set the mosaic stud size to match the image's aspect ratio
  dimensions = LegoMosaicBuilder.getFitDimensions(this.originalImageCanvas.width, this.originalImageCanvas.height, this.mosaicMaxStudWidth, this.mosaicMaxStudHeight);
  this.mosaicStudWidth  = dimensions.width;
  this.mosaicStudHeight = dimensions.height;
};


LegoMosaicBuilder.prototype.refreshMosaicImageData = function() {
  // scale the original image to the size of the mosaic
  var scaledCanvas = LegoMosaicBuilder.scaleImage(this.originalImageCanvas, this.mosaicStudWidth, this.mosaicStudHeight);
  
  this.mosaicImageData = scaledCanvas.ctx.getImageData(0, 0, this.mosaicStudWidth, this.mosaicStudHeight);
  
  // TODO: manipulate this.mosaicImageData so it has only mosaic colors
  /*for (var i = 0; i < this.mosaicImageData.data.length; i += 4) {
    this.mosaicImageData.data[i    ] = 255 - this.mosaicImageData.data[i    ]; // r
    this.mosaicImageData.data[i + 1] = 255 - this.mosaicImageData.data[i + 1]; // g
    this.mosaicImageData.data[i + 2] = 255 - this.mosaicImageData.data[i + 2]; // b
    // alpha
  }*/
};


LegoMosaicBuilder.prototype.redrawMosaic = function() {
  if (!this.mosaicImageData) return;
  
  this.canvas.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
  
  this.canvas.width  = this.mosaicMaxStudWidth;
  this.canvas.height = this.mosaicMaxStudHeight;
  
  this.canvas.ctx.putImageData(this.mosaicImageData, 0, 0);
};


/**
 * Get Fill Dimensions
 * 
 * Calculates the width and height of a rectangle scaled to fill another
 * rectangle maintaining the aspect ratio.
 * 
 * @param width      - Width of the rectangle to scale.
 * @param height     - Height of the rectangle to scale.
 * @param fillWidth  - Width of the rectangle to fill.
 * @param fillHeight - Height of the rectangle to fill.
 * @returns {<br />
 *   width: Width of the scaled rectangle.<br />
 *   height: Height of the scaled rectangle.<br />
 * }
 */
LegoMosaicBuilder.getFillDimensions = function(width, height, fillWidth, fillHeight) {
  var finalWidth, finalHeight;
  
  if (width === 0 || height === 0 || fillWidth === 0 || fillHeight === 0) {
    finalWidth = 0;
    finalHeight = 0;
  }
  else {
    var dWidth  = fillWidth - width;
    var dHeight = fillHeight - height;
    
    if (dWidth > dHeight) {
      finalWidth = fillWidth;
      finalHeight = Math.round(finalWidth * (height / width));
    }
    else {
      finalHeight = fillHeight;
      finalWidth = Math.round(finalHeight * (width / height));
    }
  }
  
  return {
    width : finalWidth,
    height: finalHeight
  };
};


/**
 * Get Fit Dimensions
 * 
 * Calculates the width and height of a rectangle scaled to fit inside
 * another rectangle maintaining the aspect ratio.
 * 
 * @param width     - Width of the rectangle to scale.
 * @param height    - Height of the rectangle to scale.
 * @param fitWidth  - Width of the rectangle to fit in.
 * @param fitHeight - Height of the rectangle to fit in.
 * @returns {<br />
 *   width: Width of the scaled rectangle.<br />
 *   height: Height of the scaled rectangle.<br />
 * }
 */
LegoMosaicBuilder.getFitDimensions = function(width, height, fitWidth, fitHeight) {
  var finalWidth, finalHeight;
  
  if (width === 0 || height === 0 || fitWidth === 0 || fitHeight === 0) {
    finalWidth = 0;
    finalHeight = 0;
  }
  else {
    var dWidth  = Math.abs(fitWidth - width);
    var dHeight = Math.abs(fitHeight - height);
    
    if (dWidth > dHeight) {
      finalWidth = fitWidth;
      finalHeight = Math.round(finalWidth * (height / width));
    }
    else {
      finalHeight = fitHeight;
      finalWidth = Math.round(finalHeight * (width / height));
    }
  }
  
  return {
    width : finalWidth,
    height: finalHeight
  };
};


/**
 * Scales an image or canvas and returns a new canvas with the scaled image
 */
LegoMosaicBuilder.scaleImage = function(image, width, height) {
  var scalingCanvas = document.createElement("canvas");
  scalingCanvas.width = width;
  scalingCanvas.height = height;
  
  scalingCanvas.ctx = scalingCanvas.getContext("2d");
  scalingCanvas.ctx.drawImage(image, 0, 0, width, height);
  
  return scalingCanvas;
};


LegoMosaicBuilder.checkCompatibility = function() {
  if (!window.File || !window.FileReader) {
    return new Error("File API not available.");
  }
  
  return null;
};
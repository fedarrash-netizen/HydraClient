class CustomTextRenderer {
    constructor(canvas) {
        this.canvas = canvas;
        this.ctx = canvas.getContext('2d');
        this.fontSize = 24;
        this.fontFamily = 'Arial';
        this.color = '#ffffff';
        this.shadowColor = 'rgba(0, 0, 0, 0.5)';
        this.shadowBlur = 4;
        this.shadowOffsetX = 2;
        this.shadowOffsetY = 2;
        this.strokeColor = '#000000';
        this.strokeWidth = 2;
        this.letterSpacing = 0;
        this.lineHeight = 1.2;
        this.animations = [];
    }

    setFont(size, family = 'Arial') {
        this.fontSize = size;
        this.fontFamily = family;
    }

    setColor(color) {
        this.color = color;
    }

    setShadow(color, blur, offsetX, offsetY) {
        this.shadowColor = color;
        this.shadowBlur = blur;
        this.shadowOffsetX = offsetX;
        this.shadowOffsetY = offsetY;
    }

    setStroke(color, width) {
        this.strokeColor = color;
        this.strokeWidth = width;
    }

    setLetterSpacing(spacing) {
        this.letterSpacing = spacing;
    }

    renderText(text, x, y, options = {}) {
        const {
            centered = false,
            animated = false,
            glowEffect = false,
            gradient = null
        } = options;

        this.ctx.save();
        
        // Apply font settings
        this.ctx.font = `${this.fontSize}px ${this.fontFamily}`;
        this.ctx.textAlign = centered ? 'center' : 'left';
        this.ctx.textBaseline = 'top';

        // Apply shadow
        this.ctx.shadowColor = this.shadowColor;
        this.ctx.shadowBlur = this.shadowBlur;
        this.ctx.shadowOffsetX = this.shadowOffsetX;
        this.ctx.shadowOffsetY = this.shadowOffsetY;

        // Apply glow effect if enabled
        if (glowEffect) {
            this.ctx.shadowColor = this.color;
            this.ctx.shadowBlur = 20;
        }

        // Apply gradient if provided
        if (gradient) {
            const grad = this.ctx.createLinearGradient(x, y, x + this.ctx.measureText(text).width, y);
            gradient.forEach((color, index) => {
                grad.addColorStop(index / (gradient.length - 1), color);
            });
            this.ctx.fillStyle = grad;
        } else {
            this.ctx.fillStyle = this.color;
        }

        // Render text with letter spacing
        if (this.letterSpacing > 0) {
            this.renderWithLetterSpacing(text, x, y, centered);
        } else {
            // Render stroke if enabled
            if (this.strokeWidth > 0) {
                this.ctx.strokeStyle = this.strokeColor;
                this.ctx.lineWidth = this.strokeWidth;
                this.ctx.strokeText(text, x, y);
            }
            
            // Render fill text
            this.ctx.fillText(text, x, y);
        }

        // Add animation if enabled
        if (animated) {
            this.addTextAnimation(text, x, y);
        }

        this.ctx.restore();
    }

    renderWithLetterSpacing(text, x, y, centered) {
        const letters = text.split('');
        let currentX = x;

        if (centered) {
            const totalWidth = this.getTotalTextWidth(text);
            currentX = x - totalWidth / 2;
        }

        letters.forEach((letter, index) => {
            // Render stroke if enabled
            if (this.strokeWidth > 0) {
                this.ctx.strokeStyle = this.strokeColor;
                this.ctx.lineWidth = this.strokeWidth;
                this.ctx.strokeText(letter, currentX, y);
            }
            
            // Render fill text
            this.ctx.fillText(letter, currentX, y);
            currentX += this.ctx.measureText(letter).width + this.letterSpacing;
        });
    }

    getTotalTextWidth(text) {
        if (this.letterSpacing > 0) {
            const letters = text.split('');
            return letters.reduce((total, letter) => {
                return total + this.ctx.measureText(letter).width + this.letterSpacing;
            }, 0) - this.letterSpacing;
        }
        return this.ctx.measureText(text).width;
    }

    renderMultilineText(text, x, y, maxWidth, options = {}) {
        const lines = this.wrapText(text, maxWidth);
        const lineHeight = this.fontSize * this.lineHeight;

        lines.forEach((line, index) => {
            const lineY = y + (index * lineHeight);
            this.renderText(line, x, lineY, options);
        });
    }

    wrapText(text, maxWidth) {
        const words = text.split(' ');
        const lines = [];
        let currentLine = '';

        words.forEach(word => {
            const testLine = currentLine + (currentLine ? ' ' : '') + word;
            const metrics = this.ctx.measureText(testLine);
            
            if (metrics.width > maxWidth && currentLine) {
                lines.push(currentLine);
                currentLine = word;
            } else {
                currentLine = testLine;
            }
        });

        if (currentLine) {
            lines.push(currentLine);
        }

        return lines;
    }

    addTextAnimation(text, x, y) {
        const animation = {
            text,
            x,
            y,
            startTime: Date.now(),
            duration: 1000,
            type: 'fadeIn'
        };
        this.animations.push(animation);
    }

    updateAnimations() {
        const currentTime = Date.now();
        this.animations = this.animations.filter(animation => {
            const elapsed = currentTime - animation.startTime;
            const progress = Math.min(elapsed / animation.duration, 1);
            
            if (animation.type === 'fadeIn') {
                this.ctx.save();
                this.ctx.globalAlpha = progress;
                this.renderText(animation.text, animation.x, animation.y);
                this.ctx.restore();
            }
            
            return progress < 1;
        });
    }

    clear() {
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
    }

    renderPixelText(text, x, y, pixelSize = 4) {
        this.ctx.save();
        
        const pixelFont = {
            'A': [[0,1,1,1,0],[1,0,0,0,1],[1,1,1,1,1],[1,0,0,0,1],[1,0,0,0,1]],
            'B': [[1,1,1,1,0],[1,0,0,0,1],[1,1,1,1,0],[1,0,0,0,1],[1,1,1,1,0]],
            'C': [[0,1,1,1,0],[1,0,0,0,1],[1,0,0,0,0],[1,0,0,0,1],[0,1,1,1,0]],
            'D': [[1,1,1,1,0],[1,0,0,0,1],[1,0,0,0,1],[1,0,0,0,1],[1,1,1,1,0]],
            'E': [[1,1,1,1,1],[1,0,0,0,0],[1,1,1,1,0],[1,0,0,0,0],[1,1,1,1,1]],
            'F': [[1,1,1,1,1],[1,0,0,0,0],[1,1,1,1,0],[1,0,0,0,0],[1,0,0,0,0]],
            'G': [[0,1,1,1,0],[1,0,0,0,1],[1,0,1,1,1],[1,0,0,0,1],[0,1,1,1,0]],
            'H': [[1,0,0,0,1],[1,0,0,0,1],[1,1,1,1,1],[1,0,0,0,1],[1,0,0,0,1]],
            'I': [[1,1,1,1,1],[0,0,1,0,0],[0,0,1,0,0],[0,0,1,0,0],[1,1,1,1,1]],
            'J': [[0,0,1,1,1],[0,0,0,1,0],[0,0,0,1,0],[1,0,0,1,0],[0,1,1,0,0]],
            'K': [[1,0,0,0,1],[1,0,0,1,0],[1,1,1,0,0],[1,0,0,1,0],[1,0,0,0,1]],
            'L': [[1,0,0,0,0],[1,0,0,0,0],[1,0,0,0,0],[1,0,0,0,0],[1,1,1,1,1]],
            'M': [[1,0,0,0,1],[1,1,0,1,1],[1,0,1,0,1],[1,0,0,0,1],[1,0,0,0,1]],
            'N': [[1,0,0,0,1],[1,1,0,0,1],[1,0,1,0,1],[1,0,0,1,1],[1,0,0,0,1]],
            'O': [[0,1,1,1,0],[1,0,0,0,1],[1,0,0,0,1],[1,0,0,0,1],[0,1,1,1,0]],
            'P': [[1,1,1,1,0],[1,0,0,0,1],[1,1,1,1,0],[1,0,0,0,0],[1,0,0,0,0]],
            'Q': [[0,1,1,1,0],[1,0,0,0,1],[1,0,0,0,1],[1,0,1,0,1],[0,1,0,1,0]],
            'R': [[1,1,1,1,0],[1,0,0,0,1],[1,1,1,1,0],[1,0,0,1,0],[1,0,0,0,1]],
            'S': [[0,1,1,1,0],[1,0,0,0,1],[0,1,1,1,0],[1,0,0,0,1],[0,1,1,1,0]],
            'T': [[1,1,1,1,1],[0,0,1,0,0],[0,0,1,0,0],[0,0,1,0,0],[0,0,1,0,0]],
            'U': [[1,0,0,0,1],[1,0,0,0,1],[1,0,0,0,1],[1,0,0,0,1],[0,1,1,1,0]],
            'V': [[1,0,0,0,1],[1,0,0,0,1],[1,0,0,0,1],[1,0,0,0,1],[0,1,1,1,0]],
            'W': [[1,0,0,0,1],[1,0,0,0,1],[1,0,1,0,1],[1,1,0,1,1],[1,0,0,0,1]],
            'X': [[1,0,0,0,1],[1,0,0,0,1],[0,1,1,1,0],[1,0,0,0,1],[1,0,0,0,1]],
            'Y': [[1,0,0,0,1],[1,0,0,0,1],[0,1,1,1,0],[0,0,1,0,0],[0,0,1,0,0]],
            'Z': [[1,1,1,1,1],[0,0,0,1,0],[0,0,1,0,0],[0,1,0,0,0],[1,1,1,1,1]],
            ' ': [[0,0,0,0,0],[0,0,0,0,0],[0,0,0,0,0],[0,0,0,0,0],[0,0,0,0,0]],
            '!': [[0,0,1,0,0],[0,0,1,0,0],[0,0,1,0,0],[0,0,0,0,0],[0,0,1,0,0]],
            '?': [[0,1,1,1,0],[1,0,0,0,1],[0,0,0,1,0],[0,0,1,0,0],[0,0,1,0,0]]
        };

        const upperText = text.toUpperCase();
        let currentX = x;

        for (let i = 0; i < upperText.length; i++) {
            const char = upperText[i];
            const pixelData = pixelFont[char] || pixelFont[' '];
            
            if (pixelData) {
                for (let row = 0; row < pixelData.length; row++) {
                    for (let col = 0; col < pixelData[row].length; col++) {
                        if (pixelData[row][col] === 1) {
                            this.ctx.fillStyle = this.color;
                            this.ctx.fillRect(
                                currentX + col * pixelSize,
                                y + row * pixelSize,
                                pixelSize,
                                pixelSize
                            );
                        }
                    }
                }
            }
            
            currentX += 6 * pixelSize; // 5 pixels + 1 pixel spacing
        }

        this.ctx.restore();
    }
}

// Export for use in browser
if (typeof module !== 'undefined' && module.exports) {
    module.exports = CustomTextRenderer;
} else {
    window.CustomTextRenderer = CustomTextRenderer;
}

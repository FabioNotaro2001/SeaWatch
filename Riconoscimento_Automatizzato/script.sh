#!/bin/bash
source ~/.bashrc
eval "$('/home/andrea/anaconda3/bin/conda' 'shell.bash' 'hook' 2> /dev/null)"
conda activate amb
cd "/var/www/html/Riconoscimento"
python3 training.py
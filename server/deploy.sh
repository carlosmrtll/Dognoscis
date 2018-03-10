# /bin/bash
if [ "$EUID" -ne 0 ]
  then echo "Please run as root."
  exit
fi

echo "Installing and updating pip..."
apt install -y python3-pip
pip3 install --updgrade

export LC_ALL=C

echo "Installing Flask..."
pip3 install flask

echo "Installing TensorFlow..."
pip3 install tensorflow

echo "Installing python setup tools"
apt-get install python-setuptools

echo "Installing gunicorn"
easy_install gunicorn

echo "Installing supervisor"
apt-get install supervisor

mkdir img

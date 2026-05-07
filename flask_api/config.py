import os

class Config:
    # MySQL settings — change these to match your local MySQL
    MYSQL_HOST = 'localhost'
    MYSQL_USER = 'root'
    MYSQL_PASSWORD = ''        # your MySQL root password
    MYSQL_DB = 'taskflow_db'
    MYSQL_CURSORCLASS = 'DictCursor'

    # JWT secret key
    JWT_SECRET_KEY = 'taskflow-secret-key-2024'

    # Flask secret
    SECRET_KEY = 'taskflow-flask-secret'

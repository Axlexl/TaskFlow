from flask import Blueprint, request, jsonify
from flask_jwt_extended import create_access_token
from app import mysql, bcrypt

auth_bp = Blueprint('auth', __name__)


@auth_bp.route('/register', methods=['POST'])
def register():
    data = request.get_json()

    # Validate input
    if not data or not data.get('username') or not data.get('password'):
        return jsonify({'message': 'Username and password are required'}), 400

    username = data['username'].strip()
    password = data['password']

    if len(username) < 3:
        return jsonify({'message': 'Username must be at least 3 characters'}), 400

    if len(password) < 6:
        return jsonify({'message': 'Password must be at least 6 characters'}), 400

    # Hash the password
    hashed_pw = bcrypt.generate_password_hash(password).decode('utf-8')

    cur = mysql.connection.cursor()
    try:
        cur.execute("INSERT INTO users (username, password) VALUES (%s, %s)", (username, hashed_pw))
        mysql.connection.commit()
        return jsonify({'message': 'User registered successfully'}), 201
    except Exception as e:
        mysql.connection.rollback()
        # Duplicate username
        return jsonify({'message': 'Username already exists'}), 409
    finally:
        cur.close()


@auth_bp.route('/login', methods=['POST'])
def login():
    data = request.get_json()

    if not data or not data.get('username') or not data.get('password'):
        return jsonify({'message': 'Username and password are required'}), 400

    username = data['username'].strip()
    password = data['password']

    cur = mysql.connection.cursor()
    try:
        cur.execute("SELECT * FROM users WHERE username = %s", (username,))
        user = cur.fetchone()

        if not user or not bcrypt.check_password_hash(user['password'], password):
            return jsonify({'message': 'Invalid username or password'}), 401

        # Create JWT token with user id as identity
        access_token = create_access_token(identity=str(user['id']))

        return jsonify({
            'message': 'Login successful',
            'token': access_token,
            'user_id': user['id'],
            'username': user['username']
        }), 200
    finally:
        cur.close()

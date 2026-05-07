from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity
from app import mysql

task_bp = Blueprint('tasks', __name__)


@task_bp.route('/', methods=['GET'])
@jwt_required()
def get_tasks():
    user_id = get_jwt_identity()
    cur = mysql.connection.cursor()
    try:
        cur.execute(
            "SELECT * FROM tasks WHERE user_id = %s ORDER BY created_at DESC",
            (user_id,)
        )
        tasks = cur.fetchall()
        return jsonify({'tasks': tasks}), 200
    finally:
        cur.close()


@task_bp.route('/<int:task_id>', methods=['GET'])
@jwt_required()
def get_task(task_id):
    user_id = get_jwt_identity()
    cur = mysql.connection.cursor()
    try:
        cur.execute(
            "SELECT * FROM tasks WHERE id = %s AND user_id = %s",
            (task_id, user_id)
        )
        task = cur.fetchone()
        if not task:
            return jsonify({'message': 'Task not found'}), 404
        return jsonify({'task': task}), 200
    finally:
        cur.close()


@task_bp.route('/', methods=['POST'])
@jwt_required()
def create_task():
    user_id = get_jwt_identity()
    data = request.get_json()

    if not data or not data.get('title'):
        return jsonify({'message': 'Title is required'}), 400

    title = data['title'].strip()
    description = data.get('description', '').strip()
    status = data.get('status', 'pending')

    if status not in ['pending', 'in_progress', 'done']:
        status = 'pending'

    cur = mysql.connection.cursor()
    try:
        cur.execute(
            "INSERT INTO tasks (user_id, title, description, status) VALUES (%s, %s, %s, %s)",
            (user_id, title, description, status)
        )
        mysql.connection.commit()
        task_id = cur.lastrowid
        return jsonify({'message': 'Task created', 'task_id': task_id}), 201
    except Exception as e:
        mysql.connection.rollback()
        return jsonify({'message': 'Failed to create task'}), 500
    finally:
        cur.close()


@task_bp.route('/<int:task_id>', methods=['PUT'])
@jwt_required()
def update_task(task_id):
    user_id = get_jwt_identity()
    data = request.get_json()

    if not data:
        return jsonify({'message': 'No data provided'}), 400

    cur = mysql.connection.cursor()
    try:
        # Check ownership
        cur.execute(
            "SELECT * FROM tasks WHERE id = %s AND user_id = %s",
            (task_id, user_id)
        )
        task = cur.fetchone()
        if not task:
            return jsonify({'message': 'Task not found'}), 404

        title = data.get('title', task['title']).strip()
        description = data.get('description', task['description'])
        status = data.get('status', task['status'])

        if status not in ['pending', 'in_progress', 'done']:
            status = task['status']

        cur.execute(
            "UPDATE tasks SET title = %s, description = %s, status = %s WHERE id = %s AND user_id = %s",
            (title, description, status, task_id, user_id)
        )
        mysql.connection.commit()
        return jsonify({'message': 'Task updated'}), 200
    except Exception as e:
        mysql.connection.rollback()
        return jsonify({'message': 'Failed to update task'}), 500
    finally:
        cur.close()


@task_bp.route('/<int:task_id>', methods=['DELETE'])
@jwt_required()
def delete_task(task_id):
    user_id = get_jwt_identity()
    cur = mysql.connection.cursor()
    try:
        cur.execute(
            "SELECT id FROM tasks WHERE id = %s AND user_id = %s",
            (task_id, user_id)
        )
        task = cur.fetchone()
        if not task:
            return jsonify({'message': 'Task not found'}), 404

        cur.execute(
            "DELETE FROM tasks WHERE id = %s AND user_id = %s",
            (task_id, user_id)
        )
        mysql.connection.commit()
        return jsonify({'message': 'Task deleted'}), 200
    except Exception as e:
        mysql.connection.rollback()
        return jsonify({'message': 'Failed to delete task'}), 500
    finally:
        cur.close()

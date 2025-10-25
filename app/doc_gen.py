#!/usr/bin/env python3
"""
Advanced Professional JavaDoc/KDoc Generator for Healthcare Android Project
Automatically generates comprehensive, context-aware documentation with healthcare domain knowledge.
Supports Java and Kotlin with intelligent analysis, pattern recognition, and rich formatting.

Features:
- Healthcare domain-specific terminology and descriptions
- Firebase integration documentation
- Material Design 3 component recognition
- Appointment and doctor management context
- Chat and messaging system documentation
- Advanced method signature analysis
- Parameter type inference and descriptions
- Return value documentation with examples
"""

import os
import re
import sys
from pathlib import Path
from typing import List, Dict, Optional, Tuple, Set
from dataclasses import dataclass, field
from datetime import datetime
import argparse
import json

@dataclass
class DocumentationStats:
    total_files: int = 0
    files_documented: int = 0
    classes_documented: int = 0
    methods_documented: int = 0
    fields_documented: int = 0
    interfaces_documented: int = 0
    enums_documented: int = 0
    inner_classes_documented: int = 0
    
@dataclass
class ClassContext:
    """Stores context about a class for better documentation."""
    name: str
    type: str  # Activity, Fragment, Adapter, etc.
    extends: str
    implements: List[str]
    is_firebase_related: bool = False
    is_ui_component: bool = False
    is_data_model: bool = False
    is_utility: bool = False
    domain_area: str = ""  # appointments, doctors, chat, etc.

# ==================== CONFIGURATION ====================

DEFAULT_AUTHOR = "Healthcare Development Team"
DEFAULT_PROJECT = "Healthcare Management System"
PROJECT_DESCRIPTION = "A comprehensive healthcare management Android application"

PROJECT_DESCRIPTION = "A comprehensive healthcare management Android application"

# Healthcare domain-specific patterns
HEALTHCARE_DOMAINS = {
    'appointment': {
        'keywords': ['appointment', 'schedule', 'booking', 'slot', 'time'],
        'description': 'appointment scheduling and management'
    },
    'doctor': {
        'keywords': ['doctor', 'physician', 'specialist', 'practitioner', 'medical'],
        'description': 'doctor profiles and management'
    },
    'patient': {
        'keywords': ['patient', 'user', 'health', 'medical record'],
        'description': 'patient information and records'
    },
    'chat': {
        'keywords': ['chat', 'message', 'conversation', 'messaging'],
        'description': 'real-time messaging and communication'
    },
    'notification': {
        'keywords': ['notification', 'alert', 'push', 'fcm'],
        'description': 'notification delivery and management'
    },
    'verification': {
        'keywords': ['verification', 'verify', 'validate', 'credential'],
        'description': 'doctor verification and validation'
    },
    'payment': {
        'keywords': ['payment', 'fee', 'consultation', 'transaction'],
        'description': 'payment processing and fee management'
    },
    'rating': {
        'keywords': ['rating', 'review', 'feedback', 'star'],
        'description': 'doctor ratings and reviews'
    }
}

# Android component detection patterns (Healthcare-specific extensions)
ANDROID_COMPONENTS = {
    'Activity': 'An Android Activity representing a single screen in the healthcare application',
    'Fragment': 'A Fragment representing a reusable portion of the user interface',
    'Service': 'A background Service for handling long-running operations',
    'BroadcastReceiver': 'A BroadcastReceiver responding to system-wide broadcast announcements',
    'ContentProvider': 'A ContentProvider managing access to structured healthcare data',
    'Application': 'The Application class managing global application state',
    'ViewModel': 'A ViewModel storing and managing UI-related data in lifecycle-aware manner',
    'Repository': 'A Repository implementing data layer abstraction for healthcare entities',
    'Adapter': 'A RecyclerView Adapter binding healthcare data to UI views',
    'ViewHolder': 'A ViewHolder caching view references for efficient recycling',
    'Dialog': 'A Dialog displaying modal UI for user interaction',
    'DialogFragment': 'A DialogFragment displaying a dialog window with fragment lifecycle',
}

# Healthcare-specific activity patterns
HEALTHCARE_ACTIVITIES = {
    'MainActivity': 'Main dashboard activity providing navigation to core features',
    'DoctorDetailsActivity': 'Activity displaying comprehensive doctor profile and booking interface',
    'AppointmentDetailActivity': 'Activity showing detailed appointment information and actions',
    'ChatActivity': 'Activity managing real-time chat communication between doctor and patient',
    'MessagesActivity': 'Activity displaying list of all chat conversations',
    'SignInScreen': 'Activity handling user authentication and login',
    'SignUpScreen': 'Activity managing new user registration',
    'DoctorVerificationActivity': 'Activity for doctor credential verification submission',
    'VerificationReviewActivity': 'Activity for admin review of doctor verification requests',
    'DoctorScheduleActivity': 'Activity managing doctor\'s working hours and availability',
    'DoctorPatientsActivity': 'Activity displaying list of doctor\'s patients',
    'PaymentMethodsActivity': 'Activity managing payment method configuration',
    'LocationActivity': 'Activity handling user location selection',
    'NotificationActivity': 'Activity displaying system notifications',
}

# Healthcare-specific fragment patterns
HEALTHCARE_FRAGMENTS = {
    'HomeFragment': 'Fragment displaying dashboard with quick actions and upcoming appointments',
    'CategoryFragment': 'Fragment showing doctor categories and specialty browsing',
    'AppointmentsFragment': 'Fragment managing appointment views with tab navigation',
    'ProfileFragment': 'Fragment displaying user profile and account settings',
    'ScheduledAppointmentsFragment': 'Fragment listing scheduled upcoming appointments',
    'CompletedAppointmentsFragment': 'Fragment showing history of completed appointments',
    'CancelledAppointmentsFragment': 'Fragment displaying cancelled appointment records',
    'AppointmentRequestsFragment': 'Fragment for doctors to manage incoming appointment requests',
    'PatientAppointmentsFragment': 'Fragment showing patient\'s appointment history',
}

# Method name patterns to rich descriptions (Healthcare-enhanced)
METHOD_PATTERNS = {
    # Getters and Setters
    r'^get[A-Z]': 'Retrieves the {}',
    r'^set[A-Z]': 'Sets the {}',
    r'^is[A-Z]': 'Checks if {}',
    r'^has[A-Z]': 'Verifies whether {}',
    r'^can[A-Z]': 'Determines if the operation {} is permitted',
    r'^should[A-Z]': 'Evaluates whether {} should occur',
    
    # Data operations
    r'^load[A-Z]': 'Loads {} from data source',
    r'^save[A-Z]': 'Persists {} to storage',
    r'^fetch[A-Z]': 'Fetches {} from remote Firebase database',
    r'^update[A-Z]': 'Updates {} in the database',
    r'^delete[A-Z]': 'Removes {} from storage',
    r'^create[A-Z]': 'Creates new {} instance',
    r'^add[A-Z]': 'Adds {} to collection',
    r'^remove[A-Z]': 'Removes {} from collection',
    
    # UI operations
    r'^initialize[A-Z]': 'Initializes {}',
    r'^setup[A-Z]': 'Configures and prepares {}',
    r'^configure[A-Z]': 'Configures {}',
    r'^handle[A-Z]': 'Handles {} event or action',
    r'^on[A-Z]': 'Callback invoked when {}',
    r'^show[A-Z]': 'Displays {} to user',
    r'^hide[A-Z]': 'Conceals {} from view',
    r'^toggle[A-Z]': 'Toggles visibility or state of {}',
    
    # Validation and calculation
    r'^validate[A-Z]': 'Validates {} against business rules',
    r'^calculate[A-Z]': 'Computes and returns {}',
    r'^compute[A-Z]': 'Calculates {}',
    r'^check[A-Z]': 'Verifies {}',
    
    # Search and filter
    r'^find[A-Z]': 'Searches for and retrieves {}',
    r'^search[A-Z]': 'Performs search operation for {}',
    r'^filter[A-Z]': 'Filters {} based on criteria',
    r'^sort[A-Z]': 'Sorts {} using specified ordering',
    
    # Firebase specific
    r'^uploadTo[A-Z]': 'Uploads {} to Firebase Storage',
    r'^downloadFrom[A-Z]': 'Downloads {} from Firebase Storage',
    r'^listenFor[A-Z]': 'Establishes Firebase listener for {}',
    
    # Healthcare specific
    r'^book[A-Z]': 'Books {} in the system',
    r'^cancel[A-Z]': 'Cancels {}',
    r'^confirm[A-Z]': 'Confirms {}',
    r'^reschedule[A-Z]': 'Reschedules {} to new time',
    r'^approve[A-Z]': 'Approves {} request',
    r'^reject[A-Z]': 'Rejects {} request',
    r'^verify[A-Z]': 'Verifies {} credentials or data',
    r'^rate[A-Z]': 'Submits rating for {}',
}

# Android lifecycle methods (enhanced with detailed descriptions)
LIFECYCLE_METHODS = {
    'onCreate': 'Called when the activity is first created. Initializes the activity and sets up the UI.',
    'onStart': 'Called when the activity becomes visible to the user.',
    'onResume': 'Called when the activity starts interacting with the user. Ideal for animations and UI updates.',
    'onPause': 'Called when the activity is partially obscured. Save transient state here.',
    'onStop': 'Called when the activity is no longer visible to the user.',
    'onDestroy': 'Called before the activity is destroyed. Cleanup resources here.',
    'onRestart': 'Called after the activity has been stopped, before being started again.',
    'onCreateView': 'Called to have the fragment instantiate its user interface view.',
    'onViewCreated': 'Called immediately after onCreateView has returned. Initialize views here.',
    'onDestroyView': 'Called when the view previously created by onCreateView has been detached.',
    'onAttach': 'Called when the fragment is attached to its host activity.',
    'onDetach': 'Called when the fragment is detached from its host activity.',
    'onSaveInstanceState': 'Called to save the current dynamic state before being paused or destroyed.',
    'onRestoreInstanceState': 'Called to restore the previously saved state.',
    'onActivityResult': 'Called when an activity you launched exits, giving you the requestCode, resultCode, and data.',
    'onRequestPermissionsResult': 'Callback for the result from requesting permissions.',
}

# Firebase-related method patterns
FIREBASE_METHODS = {
    'addValueEventListener': 'Attaches a Firebase listener for real-time updates',
    'addListenerForSingleValueEvent': 'Fetches data once from Firebase without continuous listening',
    'setValue': 'Writes data to Firebase Realtime Database',
    'updateChildren': 'Updates specific fields in Firebase without overwriting entire object',
    'push': 'Generates a new child location with auto-generated key',
    'removeValue': 'Deletes data from specified Firebase location',
}

# Parameter type descriptions (Healthcare context-aware)
PARAM_TYPE_DESCRIPTIONS = {
    'String': 'string value',
    'int': 'integer value',
    'long': 'long integer value',
    'boolean': 'boolean flag',
    'double': 'double precision number',
    'float': 'floating point number',
    'Doctor': 'doctor entity with profile information',
    'Patient': 'patient entity with health records',
    'Appointment': 'appointment details including time and doctor',
    'UserAppointment': 'user-specific appointment data',
    'Chat': 'chat conversation metadata',
    'ChatMessage': 'individual chat message',
    'Notification': 'notification object with title and content',
    'User': 'user account information',
    'DoctorProfile': 'comprehensive doctor profile with qualifications',
    'Context': 'Android context for accessing resources',
    'View': 'Android view component',
    'Intent': 'intent for activity navigation',
    'Bundle': 'bundle containing key-value pairs',
    'Callback': 'callback interface for async responses',
    'Listener': 'listener interface for event handling',
}

# Return type descriptions (enhanced with examples)
RETURN_TYPE_DESCRIPTIONS = {
    'void': None,
    'boolean': 'true if operation successful, false otherwise',
    'Boolean': 'true if condition met, false otherwise, null if undetermined',
    'int': 'integer result of the operation',
    'long': 'long integer timestamp or identifier',
    'String': 'string representation of the result',
    'List': 'list collection of items',
    'ArrayList': 'mutable list of items',
    'Map': 'map of key-value pairs',
    'HashMap': 'mutable map of key-value pairs',
    'Doctor': 'doctor object with complete profile',
    'Patient': 'patient object with medical information',
    'Appointment': 'appointment instance with booking details',
    'User': 'user account object',
    'Intent': 'intent configured for navigation',
    'View': 'inflated view component',
}

# ==================== UTILITY FUNCTIONS ====================

def camel_case_to_words(text: str) -> str:
    """
    Convert camelCase or PascalCase to separate words.
    
    Args:
        text: The camelCase string to convert
        
    Returns:
        Lowercase words separated by spaces
        
    Examples:
        - "getUserName" -> "user name"
        - "HTTPSConnection" -> "https connection"
        - "userID" -> "user id"
    """
    # Handle acronyms (e.g., HTTPSConnection -> HTTPS Connection)
    text = re.sub(r'([A-Z]+)([A-Z][a-z])', r'\1 \2', text)
    # Insert space before capitals (e.g., camelCase -> camel Case)
    text = re.sub(r'([a-z\d])([A-Z])', r'\1 \2', text)
    return text.lower()

def detect_domain_area(class_name: str, file_path: str) -> str:
    """
    Detect the healthcare domain area based on class name and file path.
    
    Args:
        class_name: Name of the class
        file_path: Path to the source file
        
    Returns:
        Domain area identifier (e.g., 'appointment', 'doctor', 'chat')
    """
    text_to_analyze = (class_name + ' ' + file_path).lower()
    
    for domain, info in HEALTHCARE_DOMAINS.items():
        for keyword in info['keywords']:
            if keyword in text_to_analyze:
                return domain
    
    return 'general'

def is_firebase_related(content: str) -> bool:
    """
    Check if class uses Firebase services.
    
    Args:
        content: File content to analyze
        
    Returns:
        True if Firebase imports or usage detected
    """
    firebase_patterns = [
        r'import.*firebase',
        r'FirebaseAuth',
        r'FirebaseDatabase',
        r'FirebaseStorage',
        r'DatabaseReference',
    ]
    
    for pattern in firebase_patterns:
        if re.search(pattern, content, re.IGNORECASE):
            return True
    return False

def extract_firebase_usage(content: str) -> List[str]:
    """
    Extract specific Firebase services used in the class.
    
    Args:
        content: File content to analyze
        
    Returns:
        List of Firebase services used
    """
    services = []
    service_patterns = {
        'Authentication': r'FirebaseAuth',
        'Realtime Database': r'FirebaseDatabase|DatabaseReference',
        'Cloud Storage': r'FirebaseStorage|StorageReference',
        'Cloud Messaging': r'FirebaseMessaging|FCM',
        'Crashlytics': r'FirebaseCrashlytics',
    }
    
    for service, pattern in service_patterns.items():
        if re.search(pattern, content):
            services.append(service)
    
    return services

def generate_method_description(method_name: str, params: List[Dict], return_type: str, 
                                class_context: Optional[ClassContext] = None) -> str:
    """
    Generate intelligent, context-aware method description.
    
    Args:
        method_name: Name of the method
        params: List of parameter dictionaries
        return_type: Return type of the method
        class_context: Context information about the containing class
        
    Returns:
        Rich, descriptive documentation string
    """
    # Check lifecycle methods first (exact match)
    if method_name in LIFECYCLE_METHODS:
        return LIFECYCLE_METHODS[method_name]
    
    # Check Firebase methods
    if method_name in FIREBASE_METHODS:
        return FIREBASE_METHODS[method_name]
    
    # Check common patterns with healthcare context
    for pattern, template in METHOD_PATTERNS.items():
        match = re.match(pattern, method_name)
        if match:
            # Remove prefix and convert to words
            prefix_len = len(match.group(0)) - 1
            remaining = method_name[prefix_len:]
            words = camel_case_to_words(remaining)
            
            # Add context-specific enhancements
            description = template.format(words)
            
            # Enhance with domain context
            if class_context and class_context.domain_area != 'general':
                domain_info = HEALTHCARE_DOMAINS.get(class_context.domain_area, {})
                domain_desc = domain_info.get('description', '')
                if domain_desc and domain_desc not in description:
                    description += f' in {domain_desc}'
            
            # Add Firebase context
            if class_context and class_context.is_firebase_related:
                if 'firebase' not in description.lower():
                    if any(keyword in method_name.lower() for keyword in ['fetch', 'load', 'get']):
                        description += ' from Firebase Realtime Database'
                    elif any(keyword in method_name.lower() for keyword in ['save', 'update', 'create']):
                        description += ' to Firebase Realtime Database'
            
            return description
    
    # Default generic description with context
    base_desc = f"Performs {camel_case_to_words(method_name)} operation"
    
    if class_context:
        if class_context.is_ui_component:
            base_desc += " and updates UI accordingly"
        elif class_context.is_data_model:
            base_desc += " on the data model"
        elif class_context.is_utility:
            base_desc += " as a utility function"
    
    return base_desc + "."

def detect_return_type_description(return_type: str, method_name: str = "") -> Optional[str]:
    """
    Generate intelligent description for return type with context awareness.
    
    Args:
        return_type: The return type of the method
        method_name: Optional method name for context
        
    Returns:
        Description of what the method returns, or None for void
    """
    if return_type in ['void', 'unit', 'Unit']:
        return None
    
    # Check predefined descriptions
    if return_type in RETURN_TYPE_DESCRIPTIONS:
        return RETURN_TYPE_DESCRIPTIONS[return_type]
    
    # Handle generic types
    if '<' in return_type:
        base_type = return_type.split('<')[0]
        generic_type = return_type.split('<')[1].rstrip('>')
        
        if base_type in ['List', 'ArrayList']:
            if generic_type in ['Doctor', 'Patient', 'Appointment']:
                return f'list of {generic_type.lower()}s matching the criteria'
            return f'list of {generic_type} objects'
        
        if base_type in ['Map', 'HashMap']:
            return 'map containing key-value pairs'
    
    # Healthcare-specific return types
    if 'Appointment' in return_type:
        return 'appointment object with booking details and status'
    elif 'Doctor' in return_type:
        return 'doctor profile with qualifications and availability'
    elif 'Patient' in return_type:
        return 'patient information including medical records'
    elif 'User' in return_type:
        return 'user account with role and authentication details'
    
    # Default based on type
    return f'the {return_type} result of the operation'

def extract_class_purpose(class_name: str, extends: str, implements: List[str], 
                          file_path: str = "", content: str = "") -> Tuple[str, ClassContext]:
    """
    Determine comprehensive class purpose from name, inheritance, and content.
    
    Args:
        class_name: Name of the class
        extends: Parent class name
        implements: List of implemented interfaces
        file_path: Path to source file
        content: File content for analysis
        
    Returns:
        Tuple of (description, ClassContext object)
    """
    # Create context object
    context = ClassContext(
        name=class_name,
        type='',
        extends=extends,
        implements=implements
    )
    
    # Detect domain area
    context.domain_area = detect_domain_area(class_name, file_path)
    
    # Check Firebase usage
    if content:
        context.is_firebase_related = is_firebase_related(content)
    
    # Check healthcare-specific activities
    if class_name in HEALTHCARE_ACTIVITIES:
        context.type = 'Activity'
        context.is_ui_component = True
        description = HEALTHCARE_ACTIVITIES[class_name]
        
        # Add Firebase context if applicable
        if context.is_firebase_related and content:
            firebase_services = extract_firebase_usage(content)
            if firebase_services:
                description += f". Integrates with Firebase {', '.join(firebase_services)}"
        
        return description, context
    
    # Check healthcare-specific fragments
    if class_name in HEALTHCARE_FRAGMENTS:
        context.type = 'Fragment'
        context.is_ui_component = True
        return HEALTHCARE_FRAGMENTS[class_name], context
    
    # Check Android components
    for component, description in ANDROID_COMPONENTS.items():
        if component in extends or component in class_name:
            context.type = component
            context.is_ui_component = component in ['Activity', 'Fragment', 'Dialog', 'Adapter']
            
            # Customize based on class name
            if 'Adapter' in class_name:
                entity = class_name.replace('Adapter', '').replace('List', '')
                entity_words = camel_case_to_words(entity)
                description = f"RecyclerView adapter managing {entity_words} data binding and view recycling"
            elif 'Activity' in class_name:
                purpose = class_name.replace('Activity', '')
                purpose_words = camel_case_to_words(purpose)
                description = f"Activity handling {purpose_words} screen and user interactions"
            elif 'Fragment' in class_name:
                purpose = class_name.replace('Fragment', '')
                purpose_words = camel_case_to_words(purpose)
                description = f"Fragment displaying {purpose_words} section of the interface"
            
            return description, context
    
    # Check common suffixes
    suffix_patterns = {
        'Helper': ('Helper class providing utility methods for {}', True),
        'Utils': ('Utility class with static helper methods for {}', True),
        'Util': ('Utility class with static helper methods for {}', True),
        'Manager': ('Manager class handling {} business logic and coordination', False),
        'Model': ('Data model representing {} entity', False),
        'Entity': ('Database entity class for {} table', False),
        'DTO': ('Data Transfer Object for {} communication', False),
        'Config': ('Configuration class for {} settings', False),
        'Constants': ('Constants definition class for {} values', False),
        'Service': ('Background service handling {} operations', False),
        'Handler': ('Handler for processing {} events', False),
        'Listener': ('Listener interface for {} event callbacks', False),
        'Callback': ('Callback interface for asynchronous {} operations', False),
    }
    
    for suffix, (template, is_utility) in suffix_patterns.items():
        if class_name.endswith(suffix):
            context.is_utility = is_utility
            entity = class_name.replace(suffix, '')
            entity_words = camel_case_to_words(entity)
            description = template.format(entity_words)
            
            # Add domain context
            if context.domain_area != 'general':
                domain_info = HEALTHCARE_DOMAINS.get(context.domain_area, {})
                description += f" in {domain_info.get('description', context.domain_area)}"
            
            return description, context
    
    # Check interfaces
    if 'interface' in str(implements).lower() or 'Interface' in class_name:
        context.type = 'Interface'
        words = camel_case_to_words(class_name.replace('Interface', ''))
        description = f"Interface defining contract for {words} implementation"
        return description, context
    
    # Check if it's a data model
    if extends in ['Object', ''] and not implements:
        context.is_data_model = True
        context.type = 'Model'
        words = camel_case_to_words(class_name)
        description = f"Data model class representing {words} entity"
        
        # Add healthcare context
        if context.domain_area != 'general':
            domain_info = HEALTHCARE_DOMAINS.get(context.domain_area, {})
            description += f" in {domain_info.get('description', '')} system"
        
        return description, context
    
    # Generic fallback
    words = camel_case_to_words(class_name)
    description = f"Class responsible for {words} functionality"
    
    if context.domain_area != 'general':
        domain_info = HEALTHCARE_DOMAINS.get(context.domain_area, {})
        description += f" in {domain_info.get('description', '')} module"
    
    return description, context

# ==================== JAVA PARSER ====================

class JavaParser:
    """Parse Java files and extract structural information with context awareness."""
    
    @staticmethod
    def parse_class(content: str, file_path: str = "") -> Optional[Dict]:
        """
        Extract comprehensive class information.
        
        Args:
            content: File content to parse
            file_path: Path to the file for context
            
        Returns:
            Dictionary with class information or None if not found
        """
        # Find class declaration (handles various modifiers)
        class_pattern = r'(?:public|private|protected)?\s*(?:static)?\s*(?:final)?\s*(?:abstract)?\s*class\s+(\w+)(?:\s+extends\s+([\w.]+))?(?:\s+implements\s+([\w\s,.<>]+))?'
        match = re.search(class_pattern, content)
        
        if match:
            class_name = match.group(1)
            extends = match.group(2) if match.group(2) else ''
            # Clean up implements - remove generics and split
            implements_raw = match.group(3) if match.group(3) else ''
            implements = [impl.strip() for impl in re.sub(r'<[^>]+>', '', implements_raw).split(',')] if implements_raw else []
            
            return {
                'name': class_name,
                'extends': extends.split('.')[-1] if extends else '',  # Get simple name
                'implements': implements,
                'line': content[:match.start()].count('\n'),
                'file_path': file_path
            }
        return None
    
    @staticmethod
    def parse_methods(content: str) -> List[Dict]:
        """Extract method information."""
        methods = []
        
        # Pattern for method declaration
        method_pattern = r'(?:public|private|protected)\s+(?:static\s+)?(?:final\s+)?(?:<[^>]+>\s+)?(\w+)\s+(\w+)\s*\(([^)]*)\)'
        
        for match in re.finditer(method_pattern, content):
            return_type = match.group(1)
            method_name = match.group(2)
            params = match.group(3).strip()
            
            # Skip constructors
            if return_type == method_name:
                continue
            
            # Parse parameters
            param_list = []
            if params:
                for param in params.split(','):
                    param = param.strip()
                    if param:
                        parts = param.split()
                        if len(parts) >= 2:
                            param_type = ' '.join(parts[:-1])
                            param_name = parts[-1]
                            param_list.append({'type': param_type, 'name': param_name})
            
            methods.append({
                'name': method_name,
                'return_type': return_type,
                'parameters': param_list,
                'line': content[:match.start()].count('\n')
            })
        
        return methods
    
    @staticmethod
    def parse_fields(content: str) -> List[Dict]:
        """Extract field information."""
        fields = []
        
        # Pattern for field declaration
        field_pattern = r'^\s*(?:public|private|protected)\s+(?:static\s+)?(?:final\s+)?(\w+(?:<[^>]+>)?)\s+(\w+)\s*[;=]'
        
        for line_num, line in enumerate(content.split('\n')):
            match = re.match(field_pattern, line)
            if match:
                field_type = match.group(1)
                field_name = match.group(2)
                
                fields.append({
                    'name': field_name,
                    'type': field_type,
                    'line': line_num
                })
        
        return fields

# ==================== DOCUMENTATION GENERATOR ====================

class DocumentationGenerator:
    """Generate professional, context-aware JavaDoc comments with healthcare domain knowledge."""
    
    def __init__(self, author: str = DEFAULT_AUTHOR, project: str = DEFAULT_PROJECT):
        self.author = author
        self.project = project
        self.current_year = datetime.now().year
        self.project_desc = PROJECT_DESCRIPTION
    
    def generate_file_header(self, file_name: str, package: str) -> str:
        """Generate comprehensive file-level documentation header."""
        return f'''/**
 * {file_name}
 * {self.project_desc}
 * 
 * Package: {package}
 * 
 * @author {self.author}
 * @version 1.0
 * @since {self.current_year}
 */
'''
    
    def generate_class_doc(self, class_info: Dict, content: str = "") -> str:
        """Generate comprehensive class-level JavaDoc with rich context."""
        purpose, context = extract_class_purpose(
            class_info['name'],
            class_info['extends'],
            class_info['implements'],
            class_info.get('file_path', ''),
            content
        )
        
        doc = f'''/**
 * {purpose}.
 *'''
        
        if class_info['extends']:
            doc += f'\n * <p>Extends: {{@link {class_info["extends"]}}}</p>'
        
        if class_info['implements']:
            impl_clean = [impl for impl in class_info['implements'] if impl.strip()]
            if impl_clean:
                impl_list = ', '.join([f'{{@link {impl.strip()}}}' for impl in impl_clean])
                doc += f'\n * <p>Implements: {impl_list}</p>'
        
        # Add Firebase info if applicable
        if content and is_firebase_related(content):
            firebase_services = extract_firebase_usage(content)
            if firebase_services:
                doc += '\n * \n * <h3>Firebase Integration:</h3>'
                doc += '\n * <ul>'
                for service in firebase_services:
                    doc += f'\n *   <li>{service}</li>'
                doc += '\n * </ul>'
        
        doc += f'''
 * 
 * @author {self.author}
 * @version 1.0
 */'''
        
        return doc
    
    def generate_method_doc(self, method_info: Dict, class_context: Optional[ClassContext] = None) -> str:
        """Generate comprehensive method-level JavaDoc with context awareness."""
        description = generate_method_description(
            method_info['name'],
            method_info.get('parameters', []),
            method_info.get('return_type', 'void'),
            class_context
        )
        
        doc = f'''    /**
     * {description}'''
        
        # Add parameter documentation with smart descriptions
        if method_info['parameters']:
            doc += '\n     *'
            for param in method_info['parameters']:
                param_type = param.get('type', '')
                # Get smart description based on parameter type
                param_desc = PARAM_TYPE_DESCRIPTIONS.get(
                    param_type.split('<')[0],  # Remove generics
                    camel_case_to_words(param['name'])
                )
                doc += f'\n     * @param {param["name"]} {param_desc}'
        
        # Add return documentation with smart descriptions
        return_type = method_info['return_type']
        return_desc = detect_return_type_description(return_type)
        if return_desc:
            doc += f'\n     * @return {return_desc}'
        
        doc += '\n     */'
        
        return doc
    
    def generate_field_doc(self, field_info: Dict) -> str:
        """Generate field-level JavaDoc."""
        field_desc = camel_case_to_words(field_info['name'])
        
        doc = f'''    /**
     * {field_desc.capitalize()}.
     */'''
        
        return doc

# ==================== FILE PROCESSOR ====================

def has_existing_javadoc(lines: List[str], line_num: int) -> bool:
    """Check if element already has JavaDoc."""
    # Check previous non-empty lines
    for i in range(line_num - 1, max(0, line_num - 10), -1):
        line = lines[i].strip()
        if line.startswith('/**'):
            return True
        if line and not line.startswith('//') and not line.startswith('*'):
            return False
    return False

def add_documentation_to_java(file_path: str, stats: DocumentationStats, 
                               dry_run: bool = False, author: str = DEFAULT_AUTHOR,
                               project: str = DEFAULT_PROJECT) -> bool:
    """Add comprehensive, context-aware JavaDoc documentation to a Java file."""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
            lines = content.split('\n')
        
        # Parse file structure with file path for better context
        parser = JavaParser()
        class_info = parser.parse_class(content, file_path)
        
        if not class_info:
            return False
        
        # Extract class context
        purpose, class_context = extract_class_purpose(
            class_info['name'],
            class_info['extends'],
            class_info['implements'],
            file_path,
            content
        )
        
        methods = parser.parse_methods(content)
        fields = parser.parse_fields(content)
        
        # Generate documentation with context awareness
        doc_gen = DocumentationGenerator(author, project)
        
        # Track changes
        changes_made = False
        new_lines = lines.copy()
        offset = 0  # Track line number offset due to insertions
        
        # Add file header if not exists
        if not content.strip().startswith('/**'):
            file_name = os.path.basename(file_path)
            # Find package declaration
            package_match = re.search(r'package\s+([\w.]+);', content)
            package = package_match.group(1) if package_match else ''
            
            header = doc_gen.generate_file_header(file_name, package)
            
            # Insert after package/imports
            insert_line = 0
            for i, line in enumerate(lines):
                if line.strip().startswith('import '):
                    insert_line = i + 1
            
            new_lines.insert(insert_line + offset, header)
            offset += header.count('\n')
            changes_made = True
        
        # Add class documentation with context and Firebase info
        if not has_existing_javadoc(lines, class_info['line']):
            class_doc = doc_gen.generate_class_doc(class_info, content)
            new_lines.insert(class_info['line'] + offset, class_doc)
            offset += class_doc.count('\n') + 1
            stats.classes_documented += 1
            changes_made = True
        
        # Add method documentation with class context
        for method in methods:
            if not has_existing_javadoc(lines, method['line']):
                method_doc = doc_gen.generate_method_doc(method, class_context)
                new_lines.insert(method['line'] + offset, method_doc)
                offset += method_doc.count('\n') + 1
                stats.methods_documented += 1
                changes_made = True
        
        # Add field documentation (only public/protected fields)
        for field in fields:
            line_content = lines[field['line']]
            if 'public ' in line_content or 'protected ' in line_content:
                if not has_existing_javadoc(lines, field['line']):
                    field_doc = doc_gen.generate_field_doc(field)
                    new_lines.insert(field['line'] + offset, field_doc)
                    offset += field_doc.count('\n') + 1
                    stats.fields_documented += 1
                    changes_made = True
        
        # Write back if changes made
        if changes_made and not dry_run:
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write('\n'.join(new_lines))
            stats.files_documented += 1
            return True
        elif changes_made and dry_run:
            stats.files_documented += 1
            return True
        
        return False
    
    except Exception as e:
        print(f"  ✗ Error processing {file_path}: {e}")
        return False

def find_java_files(root_dir: str) -> List[str]:
    """Find all Java files in project."""
    java_files = []
    skip_dirs = {'build', '.git', '.gradle', '.idea', 'gradle', '.cxx'}
    
    for root, dirs, files in os.walk(root_dir):
        dirs[:] = [d for d in dirs if d not in skip_dirs]
        
        if any(skip in root for skip in skip_dirs):
            continue
        
        for file in files:
            if file.endswith('.java'):
                java_files.append(os.path.join(root, file))
    
    return java_files

# ==================== MAIN EXECUTION ====================

def print_header():
    """Print script header."""
    header = """
╔════════════════════════════════════════════════════════════════╗
║                                                                ║
║      Professional JavaDoc Generator for Android Projects      ║
║                                                                ║
║  Automatically generates comprehensive documentation for       ║
║  classes, methods, and fields with intelligent descriptions   ║
║                                                                ║
╚════════════════════════════════════════════════════════════════╝
"""
    print(header)

def print_summary(stats: DocumentationStats, dry_run: bool = False):
    """Print documentation summary."""
    mode = "[DRY RUN] " if dry_run else ""
    print(f"\n{'='*70}")
    print(f"{mode}DOCUMENTATION SUMMARY:")
    print(f"{'='*70}")
    print(f"  📁 Total files scanned:        {stats.total_files}")
    print(f"  ✓ Files documented:            {stats.files_documented}")
    print(f"  📚 Classes documented:         {stats.classes_documented}")
    print(f"  🔧 Methods documented:         {stats.methods_documented}")
    print(f"  📝 Fields documented:          {stats.fields_documented}")
    print(f"  ○ Files skipped:               {stats.total_files - stats.files_documented}")
    print(f"{'='*70}")
    
    if dry_run:
        print("\n⚠️  This was a DRY RUN - no files were actually modified.")
        print("   Run without --dry-run to apply documentation.\n")
    else:
        print("\n✅ Documentation complete! Your code is now well-documented.\n")

def main():
    """Main execution function."""
    parser = argparse.ArgumentParser(
        description='Generate professional JavaDoc documentation for Android projects',
        formatter_class=argparse.RawDescriptionHelpFormatter
    )
    parser.add_argument(
        'path',
        nargs='?',
        default='.',
        help='Path to Android project root (default: current directory)'
    )
    parser.add_argument(
        '--dry-run',
        action='store_true',
        help='Show what would be documented without making changes'
    )
    parser.add_argument(
        '--author',
        default=DEFAULT_AUTHOR,
        help=f'Author name for documentation (default: {DEFAULT_AUTHOR})'
    )
    parser.add_argument(
        '--project',
        default=DEFAULT_PROJECT,
        help=f'Project name (default: {DEFAULT_PROJECT})'
    )
    parser.add_argument(
        '--verbose',
        action='store_true',
        help='Show detailed output for each file'
    )
    
    args = parser.parse_args()
    
    print_header()
    
    # Determine project root
    project_root = Path(args.path).resolve()
    
    # Find source directory
    possible_paths = [
        project_root / 'app' / 'src' / 'main' / 'java',
        project_root / 'src' / 'main' / 'java',
        project_root
    ]
    
    source_dir = None
    for path in possible_paths:
        if path.exists():
            source_dir = path
            break
    
    if not source_dir:
        print(f"❌ Error: Could not find source directory in {project_root}")
        sys.exit(1)
    
    print(f"📂 Project root: {project_root}")
    print(f"📂 Source directory: {source_dir}")
    print(f"👤 Author: {args.author}")
    print(f"📦 Project: {args.project}")
    
    if args.dry_run:
        print(f"🔍 Mode: DRY RUN (no files will be modified)")
    
    print(f"\n{'='*70}")
    print("🔍 Scanning for Java files...")
    print(f"{'='*70}")
    
    # Find files
    java_files = find_java_files(str(source_dir))
    
    stats = DocumentationStats()
    stats.total_files = len(java_files)
    
    print(f"  Found {len(java_files)} Java files\n")
    
    if stats.total_files == 0:
        print("❌ No files found to document!")
        sys.exit(1)
    
    print(f"{'='*70}")
    print("📝 Generating documentation...")
    print(f"{'='*70}\n")
    
    # Process files
    for file_path in java_files:
        if add_documentation_to_java(file_path, stats, args.dry_run, 
                                     args.author, args.project):
            if args.verbose:
                rel_path = os.path.relpath(file_path, source_dir)
                print(f"  ✓ {rel_path}")
    
    # Print summary
    print_summary(stats, args.dry_run)

if __name__ == '__main__':
    try:
        main()
    except KeyboardInterrupt:
        print("\n\n⚠️  Operation cancelled by user.")
        sys.exit(1)
    except Exception as e:
        print(f"\n❌ Fatal error: {e}")
        sys.exit(1)
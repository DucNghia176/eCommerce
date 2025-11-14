function authorizeRole(requiredRoles) {
    return (req, res, next) => {
        if (!req.user) {
            return res.status(401).json({success: false, message: 'Unauthorized'});
        }

        const roles = Array.isArray(req.user.roles) ? req.user.roles : [];

        // Nếu requiredRoles là string thì chuyển thành mảng
        const checkRoles = Array.isArray(requiredRoles) ? requiredRoles : [requiredRoles];

        const hasRole = checkRoles.some(role => roles.includes(role));

        if (!hasRole) {
            return res.status(403).json({success: false, message: 'Forbidden: insufficient role'});
        }
        next();
    };
}

module.exports = authorizeRole;
